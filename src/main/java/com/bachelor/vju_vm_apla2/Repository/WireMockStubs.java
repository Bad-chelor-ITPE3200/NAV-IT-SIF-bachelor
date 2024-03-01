package com.bachelor.vju_vm_apla2.Repository;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

@Configuration
public class WireMockStubs {

    static private WireMockServer wireMockServer;

    @Autowired
    public WireMockStubs(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    @PostConstruct
    public void configureStubs() {
        wireMockServer.start();
        // todo :  adding edge cases for calls that needs to be authenticated
        // todo : Add errorcode handling

        //////////////////////////////////////////////////////////////STUBS FOR SØKEFELT UTEN FILTER/////////////////////////////////////////////////////////////
//todo: 401 and 500 here
        wireMockServer.stubFor(post(urlEqualTo("/mock-journalpost")).willReturn(aResponse().withStatus(401).withBody(UNAUTHORIZED.reasonPhrase())));
        wireMockServer.stubFor(get(urlEqualTo("/mock-journalpost")).willReturn(aResponse().withStatus(500).withBody(INTERNAL_SERVER_ERROR.reasonPhrase())));
        //Mock for søkeresultat "69". Gir response basert på brukerID input fra clienten.
        wireMockServer.stubFor(post(urlEqualTo("/mock-journalpost"))
                .withRequestBody(equalToJson("{\"dokumentoversiktBruker\":\"001\"}", true, true))
                .withHeader("Authorizaton", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody("{\n" +
                                "   \"data\":{\n" +
                                "      \"dokumentoversiktBruker\":{\n" +
                                "         \"journalposter\":[\n" +
                                "            {\n" +
                                "               \"journalpostId\":\"429111291\",\n" +
                                "               \"tittel\":\"Svak Postkasse\",\n" +
                                "               \"journalposttype\":\"U\",\n" +
                                "               \"journalstatus\":\"FERDIGSTILT\",\n" +
                                "               \"tema\":\"OPP\",\n" +
                                "               \"dokumenter\":[\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00001111\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  },\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00002222\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  },\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00003333\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  }\n" +
                                "               ]\n" +
                                "            },\n" +
                                "            {\n" +
                                "               \"journalpostId\":\"429108246\",\n" +
                                "               \"tittel\":\"Rusten Veikryss\",\n" +
                                "               \"journalposttype\":\"U\",\n" +
                                "               \"journalstatus\":\"FERDIGSTILT\",\n" +
                                "               \"tema\":\"OPP\",\n" +
                                "               \"dokumenter\":[\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00004444\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  },\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00005555\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  }\n" +
                                "               ]\n" +
                                "            },\n" +
                                "           {\n" +
                                "               \"journalpostId\":\"428965411\",\n" +
                                "               \"tittel\":\"Rusten Veikryss\",\n" +
                                "               \"journalposttype\":\"U\",\n" +
                                "               \"journalstatus\":\"EKSPEDERT\",\n" +
                                "               \"tema\":\"OPP\",\n" +
                                "               \"dokumenter\":[\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00006666\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  }\n" +
                                "               ]\n" +
                                "            },\n" +
                                "            {\n" +
                                "               \"journalpostId\":\"429101111\",\n" +
                                "               \"tittel\":\"Heisann sveisann\",\n" +
                                "               \"journalposttype\":\"I\",\n" +
                                "               \"journalstatus\":\"JOURNALFOERT\",\n" +
                                "               \"tema\":\"SYM\",\n" +
                                "               \"dokumenter\":[\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00007777\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  },\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00008888\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  },\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00009999\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  },\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00010000\",\n" +
                                "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                "                  }\n" +
                                "               ]\n" +
                                "            }\n" +
                                "         ]\n" +
                                "      }\n" +
                                "   }\n" +
                                "}"
                        )
                )
        );

        //

        //Mock for å returnere pdf filer baset på søk etter journalpostID/dokumentinfoID.
        //Nå tar den bare i mot dokumentinfoid som input og returnerer pdf md samme verdi.
        // Eksempel på en spesifikk stub for dokumentID "00001111"
        wireMockServer.stubFor(get("/mock/rest/hentdokument/001/").willReturn(aResponse().
                withStatus(403).withBody(UNAUTHORIZED.reasonPhrase()))); //According to OPENAPI spec by nav, this is what a 403 should return?
        wireMockServer.stubFor(post("/mock/rest/hentdokument/001/").
                willReturn(aResponse().
                        withStatus(405).
                        withBody(METHOD_NOT_ALLOWED.reasonPhrase())));
        wireMockServer.stubFor(get(urlPathMatching("/mock/rest/hentdokument/001"))
                .willReturn(aResponse().withStatus(500).
                        withBody(INTERNAL_SERVER_ERROR.reasonPhrase())));
        wireMockServer.stubFor(get("/mock/rest/hentdokument/001/").withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/pdf")
                        .withStatus(200).withBodyFile("example.pdf")));


        //Mock for søkeresultat "002". Gir response basert på brukerID input fra clienten.
        wireMockServer.
                stubFor(post(urlEqualTo("/mock/graphql")).
                        withRequestBody(equalToJson("{\"dokumentoversiktBruker\":\"002\"}", true, true)).
                        willReturn(aResponse().
                                withStatus(401).withBody(UNAUTHORIZED.reasonPhrase())));
        wireMockServer.stubFor(post("/mock/graphql").willReturn(aResponse().
                withStatus(404).
                withBody(NOT_FOUND.toString())));
        wireMockServer.stubFor(get("/mock/graphql").
                willReturn(aResponse().
                        withStatus(500). //500 when you tries to use wrong method -> maybe a more detailed response where you can see WHAT you are doing wrong?
                        withBody(INTERNAL_SERVER_ERROR.reasonPhrase()))); //standard internal server errors for now, maybe consider INTERNAL_SERVER_ERROR?
        //todo: maybe more stubs here?
        wireMockServer.stubFor(post(urlEqualTo("/mock/graphql"))
                .withRequestBody(equalToJson("{\"dokumentoversiktBruker\":\"002\"}", true, true)).withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody("{" +
                                "\"data\":{\n" +
                                "      \"dokumentoversiktBruker\":{\n" +
                                "         \"journalposter\":[\n" +
                                "            {\n" +
                                "               \"journalpostId\":\"666111111\",\n" +
                                "               \"tittel\":\"Hemmelig dokument\",\n" +
                                "               \"journalposttype\":\"U\",\n" +
                                "               \"journalstatus\":\"FERDIGSTILT\",\n" +
                                "               \"tema\":\"XYZ\",\n" +
                                "               \"dokumenter\":[\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00006666\",\n" +
                                "                     \"tittel\":\"Topphemmelig.pdf\"\n" +
                                "                  }\n" +
                                "               ]\n" +
                                "            },\n" +
                                "            {\n" +
                                "               \"journalpostId\":\"666222222\",\n" +
                                "               \"tittel\":\"Enda et hemmelig dokument\",\n" +
                                "               \"journalposttype\":\"I\",\n" +
                                "               \"journalstatus\":\"JOURNALFOERT\",\n" +
                                "               \"tema\":\"ABC\",\n" +
                                "               \"dokumenter\":[\n" +
                                "                  {\n" +
                                "                     \"dokumentInfoId\":\"00007777\",\n" +
                                "                     \"tittel\":\"VeldigHemmelig.pdf\"\n" +
                                "                  }\n" +
                                "               ]\n" +
                                "            }\n" +
                                "         ]\n" +
                                "      }\n" +
                                "   }" +
                                "}")));


    // WireMock stub with an expanded request body along with a response with fraDato and tilDato
        wireMockServer.stubFor(post(urlEqualTo("/mock/graphql"))
            .withRequestBody(equalToJson("{\n" +
            "  \"brukerId\": {\n" +
            "    \"id\": \"020199883344\",\n" +
            "    \"type\": \"FNR\"\n" +
            "  },\n" +
            "  \"fraDato\": \"2020-01-01\",\n" +
            "  \"tilDato\": \"2021-01-01\",\n" +
            "  \"journalposttyper\": [\"I\"],\n" +
            "  \"journalstatuser\": [\"FERDIGSTILT\"],\n" +
            "  \"tema\": [\"SYM\"]\n" +
            "}", true, true))
            .withHeader("Authorization", containing("Bearer"))
            .willReturn(aResponse()
                .withHeader("Access-Control-Allow-Origin", "*") // Allow requests from all origins
                        .withHeader("Content-Type", "application/json") // Set the correct Content-Type for the response
                        .withStatus(200) // Return HTTP 200 OK
                        .withBody("{\n" +
                                          "   \"data\":{\n" +
                                          "      \"dokumentoversiktBruker\":{\n" +
                                          "         \"journalposter\":[\n" +
                                          "            {\n" +
                                          "               \"journalpostId\":\"429111291\",\n" +
                                          "               \"tittel\":\"Svak Postkasse\",\n" +
                                          "               \"journalposttype\":\"I\",\n" +
                                          "               \"journalstatus\":\"FERDIGSTILT\",\n" +
                                          "               \"tema\":\"SYM\",\n" +
                                          "               \"datoOpprettet\":\"2020-05-20\",\n" +
                                          "               \"dokumenter\":[\n" +
                                          "                  {\n" +
                                          "                     \"dokumentInfoId\":\"00001111\",\n" +
                                          "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                          "                  }\n" +
                                          "               ]\n" +
                                          "            },\n" +
                                          "            {\n" +
                                          "               \"journalpostId\":\"429108246\",\n" +
                                          "               \"tittel\":\"Rusten Veikryss\",\n" +
                                          "               \"journalposttype\":\"I\",\n" +
                                          "               \"journalstatus\":\"FERDIGSTILT\",\n" +
                                          "               \"tema\":\"SYM\",\n" +
                                          "               \"datoOpprettet\":\"2020-07-15\",\n" +
                                          "               \"dokumenter\":[\n" +
                                          "                  {\n" +
                                          "                     \"dokumentInfoId\":\"00002222\",\n" +
                                          "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                          "                  }\n" +
                                          "               ]\n" +
                                          "            },\n" +
                                          "            {\n" +
                                          "               \"journalpostId\":\"429101111\",\n" +
                                          "               \"tittel\":\"Heisann sveisann\",\n" +
                                          "               \"journalposttype\":\"I\",\n" +
                                          "               \"journalstatus\":\"FERDIGSTILT\",\n" +
                                          "               \"tema\":\"SYM\",\n" +
                                          "               \"datoOpprettet\":\"2021-01-01\",\n" +
                                          "               \"dokumenter\":[\n" +
                                          "                  {\n" +
                                          "                     \"dokumentInfoId\":\"00003333\",\n" +
                                          "                     \"tittel\":\"MASKERT_FELT\"\n" +
                                          "                  }\n" +
                                          "               ]\n" +
                                          "            }\n" +
                                          "         ]\n" +
                                          "      }\n" +
                                          "   }\n" +
                                          "}")));
    }


    @PreDestroy
    public void stopWireMockServer() {
        wireMockServer.stop();
    }
}
