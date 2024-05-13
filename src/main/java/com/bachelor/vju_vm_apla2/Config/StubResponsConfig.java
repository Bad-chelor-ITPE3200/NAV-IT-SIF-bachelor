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


public class StubResponsConfig extends ResponseDefinitionTransformer {

    @Override
    public ResponseDefinition transform(Request request,
                                        ResponseDefinition responseDefinition,
                                        com.github.tomakehurst.wiremock.common.FileSource files,
                                        com.github.tomakehurst.wiremock.extension.Parameters parameters) {
        String path = request.getUrl();
        String dokumentId = path.substring(path.lastIndexOf('/') + 1);


        return ResponseDefinitionBuilder.like(responseDefinition)
                .but()
                .withHeader("Content-Type", "application/pdf")
                .withHeader("Content-Disposition", "inline; filename=\"" + dokumentId + ".pdf\"")
                .withBodyFile(dokumentId + ".pdf")
                .build();
    }

    @Override
    public String getName() {
        return "dynamic-pdf-response-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }
}

