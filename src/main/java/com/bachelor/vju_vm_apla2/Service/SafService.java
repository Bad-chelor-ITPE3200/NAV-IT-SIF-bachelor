package com.bachelor.vju_vm_apla2.Service;

import com.bachelor.vju_vm_apla2.Config.CustomClientException;
import com.bachelor.vju_vm_apla2.Config.ErrorHandling;
import com.bachelor.vju_vm_apla2.Models.DTO.Saf.GetJournalpostList_DTO;
import com.bachelor.vju_vm_apla2.Models.DTO.Saf.ReturnFromGraphQl_DTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.springframework.http.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SafService {

    private static final Logger logger = LogManager.getLogger(SimpleService.class);
    private final WebClient webClient;
    private final String url = "http://localhost:8080"; // Eksempelbase URL for WireMock-serveren

    public SafService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    /**
     * Henter en liste over journalposter basert på innsendte søkekriterier fra klienten.
     * Denne metoden tar imot søkekriterier i form av et GetJournalpostList_DTO objekt og HttpHeaders for autentisering og andre header-verdier.
     * Den bygger en GraphQL-forespørsel basert på de innsendte kriteriene, utfører forespørselen til en konfigurert URI,
     * og returnerer en Mono som inneholder en liste med journalposter pakket i et ReturnFromGraphQl_DTO objekt.
     * Feilhåndtering implementeres ved å sjekke responsstatus for feil og anvende en enhetlig feilhåndteringsmetode for å returnere passende feilmeldinger.
     *
     * @param query Inneholder søkekriterier for å finne journalposter.
     * @param originalHeader HttpHeaders som inkluderer nødvendige autentiseringsinformasjon og andre headere.
     * @return Mono<ReturnFromGraphQl_DTO> som inneholder resultatet av forespørselen, pakket som et ReturnFromGraphQl_DTO objekt.
     */
    public Mono<ReturnFromGraphQl_DTO> hentJournalpostListe(GetJournalpostList_DTO query, HttpHeaders originalHeader) {
        String graphQLQuery = createGraphQLQuery(query);
        logger.info("Starter henting av journalposter med forespørsel: {}", graphQLQuery);

        return this.webClient.post()
                .uri(url + "/mock/graphql")
                .headers(h -> h.addAll(originalHeader))
                .bodyValue(graphQLQuery)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            int statusValue = clientResponse.statusCode().value();
                            String errorMessage = String.format("Feil ved kall til ekstern tjeneste: %d - %s", statusValue, errorBody);
                            logger.error(errorMessage);
                            return Mono.error(new CustomClientException(statusValue, errorMessage));
                        }))
                .bodyToMono(ReturnFromGraphQl_DTO.class)
                .onErrorResume(e -> {
                    // Sjekk om feilen er en CustomClientException for spesifikk feilhåndtering
                    if (e instanceof CustomClientException) {
                        CustomClientException cce = (CustomClientException) e;
                        // Logg feilmeldingen med feilkoden for spesifikk feil
                        logger.error("Spesifikk klientfeil oppstod med statuskode {}: {}", cce.getStatusCode(), cce.getMessage());
                        // Returner Mono med en ny DTO som inneholder feilmeldingen
                        return Mono.just(new ReturnFromGraphQl_DTO(cce.getMessage()));
                    } else {
                        // For alle andre typer feil, logg dem som generelle serverfeil
                        logger.error("Generell feil oppstod: ", e);
                        // Returner en generisk feilmelding til klienten
                        return Mono.just(new ReturnFromGraphQl_DTO("En uventet feil oppstod, vennligst prøv igjen senere."));
                    }
                });
    }

    /**
     * Bygger en GraphQL-forespørsel som en streng, basert på søkekriteriene mottatt fra klienten.
     * Denne metoden tar søkekriteriene, formaterer datoer korrekt, og setter sammen en gyldig GraphQL-forespørsel.
     *
     * @param query Objekt som inneholder søkekriteriene fra klienten.
     * @return En streng som representerer den komplette GraphQL-forespørselen.
     */
    private String createGraphQLQuery(GetJournalpostList_DTO query) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedFraDato = query.getFraDato() != null ? LocalDate.ofInstant(Instant.parse(query.getFraDato()), ZoneId.systemDefault()).format(formatter) : "null";
        String formattedTilDato = query.getTilDato() != null ? LocalDate.ofInstant(Instant.parse(query.getTilDato()), ZoneId.systemDefault()).format(formatter) : "null";

        String graphQLQuery = String.format(""" 
            query {
              dokumentoversiktBruker(
                brukerId: { id: "%s", type: "%s" }
                fraDato: "%s"
                tilDato: "%s"
                journalposttyper: %s
                journalstatuser: %s
                tema: %s
              ) {
                journalposter {
                    journalpostId
                    tittel
                    journalposttype
                    journalstatus
                    tema
                  dokumenter {
                    originalJournalpostId
                    tittel
                    dokumentInfoId
                  }
                }
              }
            }""", query.getBrukerId().getId(), query.getBrukerId().getType(),
                formattedFraDato, formattedTilDato,
                query.getJournalposttyper().toString(),
                query.getJournalstatuser().toString(),
                query.getTema().toString());


        logger.debug("GraphQL query generert: {}", graphQLQuery);
        return graphQLQuery;
    }

    /**
     * Utfører en HTTP GET-forespørsel for å hente et spesifikt dokument basert på dokumentInfoId og journalpostId.
     * Denne metoden kommuniserer med en ekstern tjeneste for å hente dokumentet og håndterer potensielle feil.
     *
     * @param dokumentInfoId Identifikatoren for dokumentet som skal hentes.
     * @param journalpostId Identifikatoren for journalposten som dokumentet tilhører.
     * @param originalHeader HttpHeaders som inkluderer nødvendige autentiseringsinformasjon.
     * @return Mono<Resource> som inneholder det hentede dokumentet, eller feilinformasjon hvis en feil oppstår.
     */
    public Mono<Resource> hentDokument(String dokumentInfoId, String journalpostId, HttpHeaders originalHeader) {
        String endpoint = String.format("/mock/rest/hentdokument/%s/%s", journalpostId, dokumentInfoId);
        logger.info("Henter dokument med ID: {} for journalpostID: {}", dokumentInfoId, journalpostId);

        return webClient.get()
                .uri(url + endpoint)
                .headers(h -> h.addAll(originalHeader))
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            int statusValue = clientResponse.statusCode().value();
                            String errorMessage = String.format("Feil ved henting av dokument: %s - %s", statusValue, errorBody);
                            logger.error(errorMessage);
                            return Mono.error(new CustomClientException(statusValue, errorMessage));
                        }))

                .bodyToMono(byte[].class) // Konverter responsen til en byte array
                .map(ByteArrayResource::new) // Konverter byte array til en ByteArrayResource
                .cast(Resource.class) // Cast the ByteArrayResource to Resource
                .onErrorResume(e -> {
                    if (!(e instanceof CustomClientException)) {
                        logger.error("En uventet feil oppstod: ", e);
                        String errorMessageForClient = "SAF API error in retrieving the documents, please try again later.";
                        String jsonErrorMessage = "{\"errorMessage\": \"" + errorMessageForClient.replace("\"", "\\\"") + "\"}";
                        ByteArrayResource errorResource = new ByteArrayResource(jsonErrorMessage.getBytes(StandardCharsets.UTF_8));
                        return Mono.just(errorResource);
                    }
                    // Viderefør CustomClientException slik at den kan håndteres oppstrøms
                    return Mono.error(e);
                });
    }


}