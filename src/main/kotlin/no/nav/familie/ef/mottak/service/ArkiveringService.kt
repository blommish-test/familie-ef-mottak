package no.nav.familie.ef.mottak.service

import no.nav.familie.ef.mottak.integration.IntegrasjonerClient
import no.nav.familie.ef.mottak.mapper.ArkiverDokumentRequestMapper
import no.nav.familie.ef.mottak.repository.VedleggRepository
import no.nav.familie.ef.mottak.repository.domain.Søknad
import no.nav.familie.ef.mottak.repository.domain.Vedlegg
import no.nav.familie.kontrakter.felles.dokarkiv.DokarkivBruker
import no.nav.familie.kontrakter.felles.dokarkiv.IdType
import no.nav.familie.kontrakter.felles.dokarkiv.OppdaterJournalpostRequest
import no.nav.familie.kontrakter.felles.dokarkiv.Sak
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ArkiveringService(private val integrasjonerClient: IntegrasjonerClient,
                        private val søknadService: SøknadService,
                        private val vedleggRepository: VedleggRepository) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun journalførSøknad(søknadId: String): String {
        val søknad: Søknad = søknadService.get(søknadId)
        val vedlegg = vedleggRepository.findBySøknadId(søknad.id)
        val journalpostId: String = send(søknad, vedlegg)
        val søknadMedJournalpostId = søknad.copy(journalpostId = journalpostId)
        søknadService.lagreSøknad(søknadMedJournalpostId)
        return journalpostId
    }

    fun ferdigstillJournalpost(søknadId: String) {
        val søknad: Søknad = søknadService.get(søknadId)
        val journalpostId: String = søknad.journalpostId ?: error("Søknad=$søknadId mangler journalpostId")

        val enheter = integrasjonerClient.finnBehandlendeEnhet(søknad.fnr)
        if (enheter.size > 1) {
            logger.warn("Fant mer enn 1 enhet for $søknadId: $enheter")
        }
        val journalførendeEnhet = enheter.firstOrNull()?.enhetId
                                  ?: error("Ingen behandlende enhet funnet for søknad=${søknadId} ")

        integrasjonerClient.ferdigstillJournalpost(journalpostId, journalførendeEnhet)
    }

    fun oppdaterJournalpost(søknadId: String) {
        val søknad: Søknad = søknadService.get(søknadId)
        val journalpostId: String = søknad.journalpostId ?: error("Søknad=$søknadId mangler journalpostId")
        val journalpost = integrasjonerClient.hentJournalpost(journalpostId)
        val infotrygdSaksnummer = søknad.saksnummer?.trim()?.let {
            integrasjonerClient.finnInfotrygdSaksnummerForSak(it, FAGOMRÅDE_ENSLIG_FORSØRGER, søknad.fnr)
        } ?: error("Søknaden mangler saksnummer - kan ikke finne infotrygdsak for søknad=$søknadId")

        logger.info("Fant infotrygdsak med saksnummer=$infotrygdSaksnummer for søknad=$søknadId")

        val oppdatertJournalpost = OppdaterJournalpostRequest(
                bruker = journalpost.bruker?.let {
                    DokarkivBruker(idType = IdType.valueOf(it.type.toString()), id = it.id)
                },
                sak = Sak(fagsakId = infotrygdSaksnummer,
                          fagsaksystem = INFOTRYGD,
                          sakstype = "FAGSAK"),
                tema = journalpost.tema,
        )

        integrasjonerClient.oppdaterJournalpost(oppdatertJournalpost, journalpostId)
    }

    private fun send(søknad: Søknad, vedlegg: List<Vedlegg>): String {
        val arkiverDokumentRequest = ArkiverDokumentRequestMapper.toDto(søknad, vedlegg)
        val dokumentResponse = integrasjonerClient.arkiver(arkiverDokumentRequest)
        return dokumentResponse.journalpostId
    }
}
