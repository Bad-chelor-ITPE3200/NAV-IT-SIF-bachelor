package com.bachelor.vju_vm_apla2.Config;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;



/*
Hovedformålet med å implementere en ResponseDefinitionTransformer er å
 tillate dynamisk generering av responser basert på forespørselsdata.
 I vårt scenario brukes dokumentID fra URL-en til å velge hvilken PDF-fil
 som skal returneres som respons. Dette gir oss fleksibiliteten til å håndtere
 en rekke forespørsler med en enkelt stub-konfigurasjon.

 Dokumentasjon: https://wiremock.org/docs/extensibility/transforming-responses/
 */

//TODO: Skrive om denne klassen
public class StubRespons_Config extends ResponseDefinitionTransformer {

    @Override
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, com.github.tomakehurst.wiremock.common.FileSource files, com.github.tomakehurst.wiremock.extension.Parameters parameters) {
        // Implementasjon her
        String path = request.getUrl();
        String dokumentId = path.substring(path.lastIndexOf('/') + 1);

        // Bygg ny respons basert på dokumentId
        return ResponseDefinitionBuilder.like(responseDefinition)
                .but()
                .withHeader("Content-Type", "application/pdf")
                .withHeader("Content-Disposition", "inline; filename=\"" + dokumentId + ".pdf\"")
                .withBodyFile(dokumentId + ".pdf") // Anta at PDF-filene er lagret med dokumentId som navn
                .build();
    }

    @Override
    public String getName() {
        return "dynamic-pdf-response-transformer"; // Samme navn som brukt i .withTransformers() i stub-konfigurasjonen
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }
}

