package com.bachelor.vju_vm_apla2.Service.DokArkiv_Service;

import com.bachelor.vju_vm_apla2.Models.DTO.DokArkiv.OppdaterJournalpost_DTO;
import com.bachelor.vju_vm_apla2.Models.POJO.Dokarkiv.CreateJournalpost;
import com.bachelor.vju_vm_apla2.Models.POJO.Dokarkiv.Dokumenter;
import com.bachelor.vju_vm_apla2.Models.POJO.Dokarkiv.Dokumentvariant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SplittingAvJournalposter_UPDATE {

    @Value("${wiremock-dok.combined}")
    private String url;

    private final WebClient webClient;

    private static final Logger logger = LogManager.getLogger(SplittingAvJournalposter_UPDATE.class);

    private final HentDokumenter_READ hentDokumenterREAD;

    private ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper for manual JSON serialization

    @Autowired
    public SplittingAvJournalposter_UPDATE(HentDokumenter_READ hentDokumenterREAD) {
        this.hentDokumenterREAD = hentDokumenterREAD;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }


    /**
     * Asynchronously updates the document IDs within a given CreateJournalpost object by executing a series of transformations
     * and replacements dictated by business rules encapsulated in the extractAndReplaceDocumentIds method.
     *
     * This method executes the following sequence of operations:
     * 1. Extraction and Replacement of IDs:
     *    - Initiates the process to extract existing document IDs and calculate their replacements using the
     *      extractAndReplaceDocumentIds method. This method returns a Mono<List<String>> representing the new document IDs.
     * 2. Application of New IDs:
     *    - Subscribes to the Mono returned by extractAndReplaceDocumentIds and updates the document IDs in the CreateJournalpost
     *      DTO with the new values once they are available.
     *    - Logs the updated DTO for verification and debugging purposes, aiding in ensuring data integrity throughout the process.
     * 3. Completion Signal:
     *    - After applying the new document IDs, the method returns a Mono<Void> that completes when all operations are finished,
     *      indicating that the DTO has been successfully updated.
     *
     * @param dto The CreateJournalpost object whose document IDs are to be updated.
     * @return Mono<Void> A Mono that signals completion of the update process, used for chaining further reactive operations if necessary.
     */ Mono<Void> updateDocumentIdsInDto(CreateJournalpost dto) {
        logger.info("2. Vi er inne i updateDocumentIdsInDto og Starting to update Document IDs in DTO: {}", dto);
        Mono<List<String>> cachedDocumentIds = extractAndReplaceDocumentIds(dto).cache();  // Cache the results of this operation

        logger.info("8. Vi er tilbake i updateDocumentIdsInDto og skal nå sette Base64 string inn i DTO med metode replaceDocumentIdsInDto");

        return cachedDocumentIds
                .flatMap(newIds -> replaceDocumentIdsInDto(dto, newIds))
                .then()
                .doOnSuccess(done -> logger.info("11. Tilbake til updateDocumentIdsInDto etter at DTO har fått nye fysiskID. Vi skal tilbake til craetejournapost"))
                .doOnError(error -> logger.error("Error updating document IDs", error));
    }

    /**
     * Updates the 'fysiskDokument' fields within the documents of a CreateJournalpost object using a list of new IDs.
     * This method iterates over each document and its variants contained in the provided DTO, replacing their existing
     * document IDs with new ones supplied in the 'newIds' list.
     *
     * Detailed Steps:
     * 1. Iterate over all documents in the provided CreateJournalpost object.
     * 2. For each document, iterate over all its variants.
     * 3. Replace the 'fysiskDokument' field of each variant with a new ID from the 'newIds' list.
     *    - The replacements are done in a sequential manner, using an incrementing counter to ensure each document variant
     *      receives a unique new ID from the list.
     * 4. Repeat the process for each document to ensure all IDs are updated.
     *    - The method performs two full passes over the document list to double-check and ensure all IDs are correctly updated.
     *
     * @param dto The CreateJournalpost object containing the documents whose IDs need updating.
     * @param newIds A list of new document IDs intended to replace the existing ones. This list must be at least as long
     *               as the number of document variants in the DTO to avoid index errors.
     */
    private Mono<Void> replaceDocumentIdsInDto(CreateJournalpost dto, List<String> newIds) {
        logger.info("9. Vi er i replaceDocumentIdsInDto og kommet inn med DTO: {} og nye IDer: {}", dto, newIds);
        int count = 0;
        for (Dokumenter dokument : dto.getDokumenter()) {
            for (Dokumentvariant variant : dokument.getDokumentvarianter()) {
                if (count < newIds.size()) {
                    String oldId = variant.getFysiskDokument();  // Lagrer gammel ID for logging
                    variant.setFysiskDokument(newIds.get(count));
                    logger.info("Erstattet gammel ID: {} med ny ID: {} for dokumentvariant", oldId, newIds.get(count));
                    count++;
                }
            }
        }
        logger.info("10. Ferdig med å erstatte IDer i DTO, oppdatert DTO: {}", dto);
        return Mono.empty();  // Returnerer en tom Mono for å signalisere fullførelse.
    }


    /**
     * Extracts and replaces all document IDs within a given CreateJournalpost object and combines the results
     * into a single list. This method processes the document IDs by calling the `processDocuments` method twice,
     * once for potentially the old IDs and once for potentially the new IDs, then merges the resulting lists into one.
     *
     * Steps:
     * 1. Call the `processDocuments` method twice using the same DTO but with the same journal post ID each time.
     *    This could be intended to handle different scenarios or conditions within the processing method.
     * 2. Each call to `processDocuments` is expected to return a list of processed (possibly replaced) document IDs.
     * 3. The two lists of IDs are then combined into a single list to consolidate all the new and old IDs.
     * 4. This consolidated list of IDs is then returned as a Mono for further asynchronous processing or handling.
     *
     * Note: The method currently uses the same DTO and the same hardcoded 'journalpostId' for both calls which might
     *       not be intended and could be a potential area for bugs if different behaviors are expected for different
     *       IDs or processing rounds.
     *
     * @param dto The CreateJournalpost object containing the documents whose IDs need to be extracted and replaced.
     * @return Mono<List<String>> A reactive Mono that emits a single list containing all the new and old document IDs
     *         combined after both processing calls.
     */
    private Mono<List<String>> extractAndReplaceDocumentIds(CreateJournalpost dto) {
        logger.info("3. Vi er inne i extractAndReplaceDocumentIds og Starting ID extraction for: {}", dto);

        // Obtain the journal post ID in a correct manner
        String journalpostId = "1";  // Replace with dynamic ID retrieval logic

        // Single call to process the documents
        return processDocuments(dto, journalpostId)
                .map(ids -> {
                    // Optionally you can handle the IDs further here if needed
                    logger.info("7. Vi er tilbake til extractAndReplaceDocumentIds og har en liste med encoded string som skal returners");
                    return ids;
                });
    }


    /**
     * Processes all document variants within a CreateJournalpost object to potentially replace their 'fysiskDokument' IDs.
     * This method asynchronously retrieves new document IDs for each variant using an external service or a random ID generator
     * and collects these new IDs into a list.
     *
     * Steps:
     * 1. Iterate over each document in the provided CreateJournalpost object and each of its variants.
     * 2. For each variant, asynchronously fetch a new ID using the `hentDokument_DokArkiv` method (or `random_hentdok_dokarkiv` for testing),
     *    which might involve calling an external document archive service to get a new document ID based on the current ID and the journal post ID.
     * 3. Log the replacement of each document ID for tracking and verification purposes.
     * 4. Collect all the asynchronous operations (Mono<String>) into a list and use Mono.zip to wait for all of them to complete.
     * 5. Once all new IDs are retrieved, combine them into a single list and return this list as the result of the Mono.
     *
     * This method ensures that all document variants in the metadata are processed concurrently for efficiency, and the results
     * are aggregated into a single list for further processing or verification.
     *
     * @param metadata The CreateJournalpost object containing the documents and their variants to be processed.
     * @param journalpostId A journal post ID used potentially in the document ID retrieval process.
     * @return Mono<List<String>> A reactive Mono that emits a list of new document IDs, one for each variant in the metadata.
     */
    private Mono<List<String>> processDocuments(CreateJournalpost metadata, String journalpostId) {
        logger.info("4. Processing documents for metadata: {} and journalPostID: {} ", metadata + " " +  journalpostId);

        // Creating a list to hold Monos of document IDs
        List<Mono<String>> documentIdMonos = new ArrayList<>();

        // Iterate over all documents and their variants to replace document IDs
        for (Dokumenter dokument : metadata.getDokumenter()) {
            for (Dokumentvariant variant : dokument.getDokumentvarianter()) {
                Mono<String> documentIdMono = hentDokumenterREAD.hentDokument_DokArkiv(variant.getFysiskDokument(), journalpostId)
                        //Mono<String> documentIdMono = random_hentdok_dokarkiv(variant.getFysiskDokument(), journalpostId)
                        .doOnNext(newId -> logger.info("6 Vi er tilbake til processDocuments og har mottat en Base64 string fra hentDokument_DokArkiv og puttet i en liste" ) )
                        .cache(); // Cache each Mono to ensure the operation is performed only once
                documentIdMonos.add(documentIdMono);
            }
        }

        // Use Mono.zip to wait for all document ID fetching operations to complete and then collect the results
        return Mono.zip(documentIdMonos, results ->
                        Stream.of(results).map(result -> (String) result).collect(Collectors.toList())
                )
                .doOnNext(allIds -> logger.info("All document IDs processed and collected: {}", allIds));
    }








    //////////////////RANDOM STRING RETURNER//////////////

    /**
     * Asynchronously generates a random 3-letter string and logs the operation.
     * This method now returns a Mono<String> to fit into a reactive programming model.
     *
     * @param dokumentId The original document ID for logging.
     * @param journalpostId The journal post ID for logging.
     * @return Mono<String> containing the new random ID.
     */
    public Mono<String> random_hentdok_dokarkiv(String dokumentId, String journalpostId) {
        return Mono.fromSupplier(() -> {
            String newId = generateRandomString(3);
            logger.info("Received physical document ID: {}, replaced with: {}", dokumentId, newId);
            return newId;
        });
    }

    /**
     * Generates a random string of a given length using characters from A to Z.
     *
     * @param length The length of the string to generate.
     * @return A random string of the specified length.
     */
    private String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char tmp = (char) ('A' + random.nextInt('Z' - 'A'));
            sb.append(tmp);
        }
        return sb.toString();
    }

    // Method to format date
    public String formatIsoDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return "{\"date\": \"" + sdf.format(date) + "\"}";
    }

    public Mono<ResponseEntity<Boolean>> oppdaterMottattDato(OppdaterJournalpost_DTO jpMetadata, HttpHeaders originalHeader) {
        logger.info("Vi er nå inn i oppdaterJournalpost for å oppdatere Journalpost med mottattDato");

        String journalpostID = jpMetadata.getJournalpostID();

        String endpoint = "/rest/journalpostapi/v1/journalpost/" + journalpostID;

        Date currentDate = jpMetadata.getMottattDato();

        String jsonPayload = formatIsoDate(currentDate);

        System.out.println("Service - oppdaterMottattDato: vi skal nå inn i wiremock med forespørsel: ");
        System.out.println("Original headers:");

        return this.webClient.put()
                .uri(url + endpoint)
                .header(HttpHeaders.AUTHORIZATION, originalHeader.getFirst(HttpHeaders.AUTHORIZATION))
                //.headers(headers -> headers.addAll(headersForRequest))
                .bodyValue(jsonPayload)
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

