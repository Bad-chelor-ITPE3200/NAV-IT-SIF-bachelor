package com.bachelor.vju_vm_apla2.Service.DokArkiv_Service;

import com.bachelor.vju_vm_apla2.Config.CustomClientException;
import com.bachelor.vju_vm_apla2.Models.DTO.Saf.GetJournalpostList_DTO;
import com.bachelor.vju_vm_apla2.Models.DTO.Saf.ReturnFromGraphQl_DTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.SQLOutput;

@Service
public class FeilRegistrer_DELETE {
    private static final Logger logger = LogManager.getLogger(HentDokumenter_READ.class);

    @Value("${wiremock-dok.combined}")
    private String url;

    private final WebClient webClient;

    public FeilRegistrer_DELETE() {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public Mono<ResponseEntity<Boolean>> feilRegistrer(String journalpostId, String type, HttpHeaders originalHeader) {
        logger.info("17. Vi er nå på å feilregistere fra opprett journalpost");
        String endpoint = "";

        String endpoint_test = "/mock/rest/test";

        if (type.equals("I")) {
            endpoint = "/rest/journalpostapi/v1/journalpost/" + journalpostId + "/feilregistrer/settStatusUtgaar";
        } else if (type.equals("U")) {
            endpoint = "/rest/journalpostapi/v1/journalpost/" + journalpostId + "/feilregistrer/settStatusAvbryt";
        } else {
            return Mono.just(ResponseEntity.badRequest().body(false)); // Returning a bad request if type is not handled
        }

        System.out.println("Service - feilregistrer: vi skal nå inn i wiremock med forespørsel: ");
        System.out.println("Original headers:");

        return this.webClient.patch()
                .uri(url + endpoint)
                .header(HttpHeaders.AUTHORIZATION, originalHeader.getFirst(HttpHeaders.AUTHORIZATION))
                //.headers(headers -> headers.addAll(headersForRequest))
                .retrieve()
                .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error("Error response body: {}", errorBody);
                                    return Mono.error(new RuntimeException("Error from downstream service: " + errorBody));
                                })
                )
                .bodyToMono(Boolean.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(true, HttpStatus.OK)) // Handle 204 No Content
                .onErrorResume(e -> {
                    logger.error("Error handling request", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false));
                })
                .doOnSuccess(response -> logger.info("Operation completed with status: {}", response.getStatusCode()))
                .doOnError(error -> logger.error("Operation failed", error));
    }
}
