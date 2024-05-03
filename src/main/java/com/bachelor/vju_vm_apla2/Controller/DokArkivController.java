package com.bachelor.vju_vm_apla2.Controller;

import com.bachelor.vju_vm_apla2.Models.DTO.DokArkiv.CreateJournalpost_DTO;
import com.bachelor.vju_vm_apla2.Models.DTO.DokArkiv.OppdaterJournalpost_DTO;
import com.bachelor.vju_vm_apla2.Models.DTO.DokArkiv.ResponeReturnFromDokArkiv_DTO;
import com.bachelor.vju_vm_apla2.Service.DokArkiv_Service.FeilRegistrer_DELETE;
import com.bachelor.vju_vm_apla2.Service.DokArkiv_Service.OpprettNyeJournalposter_CREATE;
import com.bachelor.vju_vm_apla2.Service.DokArkiv_Service.SplittingAvJournalposter_UPDATE;
import lombok.Getter;
import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.api.Unprotected;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Protected
@RestController
public class DokArkivController {

    private static final Logger logger = LogManager.getLogger(DokArkivController.class);

    private final SplittingAvJournalposter_UPDATE oppdaterJournalposter;
    private final OpprettNyeJournalposter_CREATE opprettNyeJournalposterCREATE;
    private final FeilRegistrer_DELETE feilRegistrerService;

    @Autowired
    public DokArkivController(OpprettNyeJournalposter_CREATE opprettNyeJournalposterCREATE, FeilRegistrer_DELETE feilRegistrerService, SplittingAvJournalposter_UPDATE oppdaterJournalposter){
        this.opprettNyeJournalposterCREATE = opprettNyeJournalposterCREATE;
        this.feilRegistrerService = feilRegistrerService;
        this.oppdaterJournalposter = oppdaterJournalposter;
    }


    //TODO: Opprett kontroller CreateJournalpost

    /////////////////////////////////// ALPHA METODE FOR CREATE JOURNALPOST///////////////////////////////////////////
//@RequestMapping("/dokarkivAPI")
    @CrossOrigin
    @PostMapping("/createJournalpost")
    public Mono<ResponseEntity<List<ResponeReturnFromDokArkiv_DTO>>> createJournalpost(@RequestBody CreateJournalpost_DTO meta, @RequestHeader HttpHeaders headers) {
        logger.info("0.1 Controller - mottat data fra klienten - Received JSON data: {}", meta);
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println(meta.getJournalpostID() + " " + meta.getOldMetadata().getJournalposttype());
        // Call the service and handle its response asynchronously
        logger.info("0.2 Controller - Nå går vi inn i createjournalpost med " + meta);
        return opprettNyeJournalposterCREATE.createJournalpost(meta, headers)
                //legge til logger eller system printout "("16. Vi er tilabke fra å opprette journalpost og skal nå feilregisterere")"
                .flatMap(response -> feilRegistrerService.feilRegistrer(meta.getJournalpostID(), meta.getOldMetadata().getJournalposttype(), headers)
                        .flatMap(feilResponse -> {
                            System.out.println("17. Dokarkiv controller - respons------------------------------------------------------------------");
                            System.out.println(feilResponse);
                            if (!feilResponse.getBody()) { // Accessing the body of ResponseEntity<Boolean>
                                logger.error("Error registering failure for journal post ID: {}", meta.getJournalpostID());
                                List<ResponeReturnFromDokArkiv_DTO> emptyList = new ArrayList<>();
                                return Mono.just(ResponseEntity.internalServerError().body(emptyList));
                            }
                            logger.info("Response received from service: {}", response.getBody());
                            return Mono.just(ResponseEntity.ok().body(response.getBody()));
                        })
                )
                .onErrorResume(e -> {
                    logger.error("Feil ved lagring av nye journalposter: {}", e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().body(new ArrayList<>()));  // Provide an empty list on error
                })
                .doOnSuccess(response -> logger.info("Response sent to client: {}", response.getStatusCode()));
    }


    @CrossOrigin
    @GetMapping("/feilregistrer")
    public Mono<ResponseEntity<Boolean>> feilregistrer(@RequestParam("journalpostId") String journalpostId, @RequestParam("type") String type, @RequestHeader HttpHeaders headers){
        return feilRegistrerService.feilRegistrer(journalpostId, type, headers)
                .onErrorResume(e -> {
                    // Handle any errors that occur during the service call
                    logger.error("Feil ved lagring av nye journalposter: {}", e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().body(false));  // Provide an empty list on error
                })
                .doOnSuccess(response -> logger.info("Response sent to client: {}", response.getStatusCode()));
    }

    @CrossOrigin
    @PostMapping("/oppdaterJournalpost")
    public Mono<ResponseEntity<Boolean>> oppdaterJournalpost(@RequestBody OppdaterJournalpost_DTO meta, @RequestHeader HttpHeaders headers){
        System.out.println("YAY!");
        return oppdaterJournalposter.oppdaterMottattDato(meta, headers)
                .onErrorResume(e -> {
                    // Handle any errors that occur during the service call
                    logger.error("Feil ved oppdatering av Journalpost: {}", e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().body(false));  // Provide an empty list on error
                })
                .doOnSuccess(response -> logger.info("Response sent to client: {}", response.getStatusCode()));
    }


}
