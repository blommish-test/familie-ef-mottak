package no.nav.familie.ef.mottak.hendelse

import no.nav.joarkjournalfoeringhendelser.JournalfoeringHendelseRecord

fun JournalfoeringHendelseRecord.skalProsesseres() = this.erTemaENF() && erHendelsetypeGyldig()

fun JournalfoeringHendelseRecord.erTemaENF() = this.temaNytt?.toString() == "ENF"

fun JournalfoeringHendelseRecord.erHendelsetypeGyldig() =
        arrayOf("MidlertidigJournalført", "TemaEndret").contains(this.hendelsesType.toString())