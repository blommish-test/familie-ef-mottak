package no.nav.familie.ef.mottak.api

import no.nav.familie.ef.mottak.api.dto.Søknad
import no.nav.familie.ef.mottak.integration.dto.Kvittering
import no.nav.familie.ef.mottak.service.SøknadService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/soknad"], produces = [APPLICATION_JSON_VALUE])
class SøknadController(val søknadService: SøknadService) {

    @PostMapping("sendInn")
    fun sendInn(@RequestBody søknad: Søknad): Kvittering {
        return søknadService.sendInn(søknad)
    }

}