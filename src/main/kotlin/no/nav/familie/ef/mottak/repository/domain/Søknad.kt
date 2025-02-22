package no.nav.familie.ef.mottak.repository.domain

import no.nav.familie.ef.mottak.encryption.FileCryptoConverter
import no.nav.familie.ef.mottak.encryption.StringValCryptoConverter
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "soknad")
data class Søknad(@Id
                  val id: String = UUID.randomUUID().toString(),
                  @Convert(converter = StringValCryptoConverter::class)
                  @Column(name = "soknad_json")
                  val søknadJson: String,
                  @Convert(converter = FileCryptoConverter::class)
                  @Column(name = "soknad_pdf")
                  val søknadPdf: Fil? = null,
                  val dokumenttype: String,
                  @Column(name = "journalpost_id")
                  val journalpostId: String? = null,
                  val saksnummer: String? = null,
                  val fnr: String,
                  @Column(name = "task_opprettet")
                  val taskOpprettet: Boolean = false,
                  @Column(name = "opprettet_tid")
                  val opprettetTid: LocalDateTime = LocalDateTime.now())
