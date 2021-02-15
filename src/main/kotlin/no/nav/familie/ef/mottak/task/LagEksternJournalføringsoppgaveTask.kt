package no.nav.familie.ef.mottak.task

import no.nav.familie.ef.mottak.repository.SoknadRepository
import no.nav.familie.ef.mottak.service.OppgaveService
import no.nav.familie.prosessering.AsyncTaskStep
import no.nav.familie.prosessering.TaskStepBeskrivelse
import no.nav.familie.prosessering.domene.Task
import no.nav.familie.prosessering.domene.TaskRepository
import org.springframework.stereotype.Service

@Service
@TaskStepBeskrivelse(taskStepType = LagEksternJournalføringsoppgaveTask.TYPE,
                     beskrivelse = "Lager oppgave i GoSys")
class LagEksternJournalføringsoppgaveTask(private val taskRepository: TaskRepository,
                                          private val oppgaveService: OppgaveService,
                                          private val soknadRepository: SoknadRepository
) : AsyncTaskStep {

    override fun doTask(task: Task) {
        val journalpostId = task.payload

        // Ved helt spesielle race conditions kan man tenke seg at vi fikk opprettet
        // denne (LagEksternJournalføringsoppgaveTask) før søknaden fikk en journalpostId
        if (lagOppgaveIkkeHåndtertINormalFlyt(journalpostId)) {
            oppgaveService.lagJournalføringsoppgaveForJournalpostId(journalpostId)
        }
    }


    private fun lagOppgaveIkkeHåndtertINormalFlyt(journalpostId: String) =
            soknadRepository.findByJournalpostId(journalpostId) == null

    override fun onCompletion(task: Task) {
        val journalpostId = task.payload
        if (lagOppgaveIkkeHåndtertINormalFlyt(journalpostId)) {
            taskRepository.save(Task(SjekkOmJournalpostHarFåttEnSak.HENT_EKSTERN_SAKSNUMMER_FRA_JOARK,
                                     task.payload,
                                     task.metadata))
        }
    }


    companion object {

        const val TYPE = "lagEksternJournalføringsoppgave"
    }

}
