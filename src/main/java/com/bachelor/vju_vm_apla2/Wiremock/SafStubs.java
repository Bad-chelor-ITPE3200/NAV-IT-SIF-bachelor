package com.bachelor.vju_vm_apla2.Wiremock;
import com.github.tomakehurst.wiremock.WireMockServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.bachelor.vju_vm_apla2.Wiremock.WireMockResponseList.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpStatus.*;


@Configuration
public class SafStubs {

    static private WireMockServer wireMockServer;

    @Autowired
    public SafStubs(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    @PostConstruct
    public void configureStubs() {
        wireMockServer.start();
        // todo :  adding edge cases for calls that needs to be authenticated
        // todo : Add errorcode handling
        wireMockServer.stubFor(get(urlEqualTo("/hello"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Hello")));


        //////////////////////////////////////////////////////////////STUBS FOR FINNE DOKUMENTER/////////////////////////////////////////////////////////////

        //Mock for å returnere pdf filer baset på søk etter journalpostID/dokumentinfoID.
        //Nå tar den bare i mot dokumentinfoid som input og returnerer pdf md samme verdi.
        // Eksempel på en spesifikk stub for dokumentID "00001111"
        wireMockServer.stubFor(get(urlPathMatching("/rest/hentdokument/1/(.*)")) //funker for MVP, men vi bør virkelig vurdere å endre på dette ved en senere anledning
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/pdf")
                        .withStatus(200)
                        .withHeader("Content-Disposition", "inline; filename=\"example.pdf\"")
                        .withTransformers("dynamic-pdf-response-transformer")));


        //////////////////////////////////////////////////////////////STUBS FOR OPPRETT JOURNALPOST/////////////////////////////////////////////////////////////


        // Stub that checks the body for an "old" or "new" indicator
        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"old\"}", true, true))
                        .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\n" +
                                "  \"dokumenter\": [\n" +
                                "    {\n" +
                                "      \"dokumentInfoId\": \"123-old\"\n" +
                                "    }\n" +
                                "  ],\n" +
                                "  \"journalpostId\": \"467010363-old\",\n" +
                                "  \"journalpostferdigstilt\": false\n" +
                                "}")));

        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"new\"}", true, true))
                        .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\n" +
                                "  \"dokumenter\": [\n" +
                                "    {\n" +
                                "      \"dokumentInfoId\": \"123-new\"\n" +
                                "    }\n" +
                                "  ],\n" +
                                "  \"journalpostId\": \"467010364-new\",\n" +
                                "  \"journalpostferdigstilt\": false\n" +
                                "}")));




        //////////////////////////////////////////////////////////////STUBS FOR OPPRETT JOURNALPOST FEILHÅNDTERING/////////////////////////////////////////////////////////////


        /*

        // 400
        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"old\"}", true, true))
                        .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody(
                                "Kan ikke opprette journalpost"
                        )
                ));

        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"new\"}", true, true))
                        .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody(
                                "Kan ikke opprette journalpost"
                        )
                ));



        // 401
        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"old\"}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(401)
                        .withBody(
                                "Mangler tilgang til å opprette ny journalpost. Ugyldig OIDC token. Denne feilen gis dersom tokenet ikke har riktig format eller er utgått."
                        )
                ));

        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"new\"}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(401)
                        .withBody(
                                "Mangler tilgang til å opprette ny journalpost. Ugyldig OIDC token. Denne feilen gis dersom tokenet ikke har riktig format eller er utgått."
                        )
                ));



        // 403
        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"old\"}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(403)
                        .withBody(
                                "Bruker mangler tilgang til å opprette journalpost på tema"
                        )
                ));

        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"new\"}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(403)
                        .withBody(
                                "Bruker mangler tilgang til å opprette journalpost på tema"
                        )
                ));

        // 409
        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"old\"}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(409)
                        .withBody(
                                "Journalpost med angitt eksternReferanseId eksisterer allerede. Ingen journalpost ble opprettet."
                        )
                ));

        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"new\"}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(409)
                        .withBody(
                                "Journalpost med angitt eksternReferanseId eksisterer allerede. Ingen journalpost ble opprettet."
                        )
                ));

        // 500
        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"old\"}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(500)
                        .withBody(
                                 "Internal server error"
                        )
                ));

        wireMockServer.stubFor(post(urlEqualTo("/rest/journapostapi/v1/journalpost?forsoekFerdigstill=false"))
                .withRequestBody(equalToJson("{\"versjon\":\"new\"}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(500)
                        .withBody(
                                "Internal server error"
                        )
                ));

         */





        //////////////////////////////////////////////////////////////STUBS FOR SØKEFELT UTEN FILTER/////////////////////////////////////////////////////////////
//todo: 401 and 500 here
//        wireMockServer.stubFor(post(urlEqualTo("/mock-journalpost")).willReturn(aResponse().withStatus(401).withBody(UNAUTHORIZED.getReasonPhrase())));
//        wireMockServer.stubFor(get(urlEqualTo("/mock-journalpost")).willReturn(aResponse().withStatus(500).withBody(INTERNAL_SERVER_ERROR.getReasonPhrase())));
        //Mock for søkeresultat "001". Gir response basert på brukerID input fra clienten.
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson("{\"dokumentoversiktBruker\":\"001\"}", true, true))
                .withHeader("Authorizaton", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody("{\n" +
                                "      \"dokumentoversikt\":{\n" +
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





        //Mock for søkeresultat "002". Gir response basert på brukerID input fra clienten.
        wireMockServer.
                stubFor(post(urlEqualTo("/graphql")).
                        withRequestBody(equalToJson("{\"dokumentoversiktBruker\":\"002\"}", true, true)).
                        willReturn(aResponse().
                                withStatus(401).withBody(UNAUTHORIZED.getReasonPhrase())));
        wireMockServer.stubFor(post("/graphql").willReturn(aResponse().
                withStatus(404).
                withBody(NOT_FOUND.toString())));
        wireMockServer.stubFor(get("/graphql").
                willReturn(aResponse().
                        withStatus(500). //500 when you tries to use wrong method -> maybe a more detailed response where you can see WHAT you are doing wrong?
                        withBody(INTERNAL_SERVER_ERROR.getReasonPhrase()))); //standard internal server errors for now, maybe consider INTERNAL_SERVER_ERROR?
        //todo: maybe more stubs here?
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson("{\"dokumentoversiktBruker\":\"002\"}", true, true)).withHeader("Authorization", containing("Bearer")));


                        //Mock for søkeresultat "003". Gir response basert på brukerID input fra clienten.
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson("{\"brukerId\": {\"id\": \"003\"}}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody("{\n" +
                                "   \"dokumentoversikt\":{\n" +
                                "      \"journalposter\":[\n" +
                                "         {\n" +
                                "            \"journalpostId\":\"666111111\",\n" +
                                "            \"tittel\":\"Hemmelig dokument\",\n" +
                                "            \"journalposttype\":\"U\",\n" +
                                "            \"journalstatus\":\"FERDIGSTILT\",\n" +
                                "            \"tema\":\"OPP\",\n" +
                                "            \"datoOpprettet\":\"2024-03-01T12:00:00Z\",\n" + // Eksempel på inkludering av datoOpprettet for første journalpost
                                "            \"dokumenter\":[\n" +
                                "               {\n" +
                                "                  \"dokumentInfoId\":\"00006666\",\n" +
                                "                  \"tittel\":\"Topphemmelig.pdf\"\n" +
                                "               }\n" +
                                "            ]\n" +
                                "         },\n" +
                                "         {\n" +
                                "            \"journalpostId\":\"666222222\",\n" +
                                "            \"tittel\":\"Enda et hemmelig dokument\",\n" +
                                "            \"journalposttype\":\"I\",\n" +
                                "            \"journalstatus\":\"JOURNALFOERT\",\n" +
                                "            \"tema\":\"OPP\",\n" +
                                "            \"datoOpprettet\":\"2024-03-02T12:00:00Z\",\n" + // Eksempel på inkludering av datoOpprettet for andre journalpost
                                "            \"dokumenter\":[\n" +
                                "               {\n" +
                                "                  \"dokumentInfoId\":\"00007777\",\n" +
                                "                  \"originalJournalpostId\":\"00007777\",\n" +
                                "                  \"tittel\":\"VeldigHemmelig.pdf\"\n" +
                                "               }\n" +
                                "            ]\n" +
                                "         }\n" +
                                "      ]\n" +
                                "   }\n" +
                                "}")));


        //Mock for søkeresultat "004". Gir response basert på brukerID input fra clienten.
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"brukerId\": {\n" +
                        "    \"id\": \"004\",\n" +
                        "    \"type\": \"FNR\"\n" +
                        "  },\n" +
                        "  \"journalposttyper\": [],\n" +
                        "  \"journalstatuser\": [],\n" +
                        "  \"tema\": []\n" +
                        "}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody("{\n" +
                                "  \"dokumentoversikt\": {\n" +
                                "    \"journalposter\": [\n" +
                                "      {\n" +
                                "        \"journalpostId\": \"666111111\",\n" +
                                "        \"tittel\": \"Hemmelig dokument\",\n" +
                                "        \"journalposttype\": \"U\",\n" +
                                "        \"journalstatus\": \"FERDIGSTILT\",\n" +
                                "        \"tema\": \"OPP\",\n" +
                                "        \"datoOpprettet\": \"2024-03-01T12:00:00Z\",\n" +
                                "        \"relevanteDatoer\": [],\n" +
                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00006666\",\n" +
                                "            \"tittel\": \"Topphemmelig.pdf\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"67298374528\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Raus Trane\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +



                                "      },\n" +
                                "      {\n" +
                                "        \"journalpostId\": \"666222222\",\n" +
                                "        \"tittel\": \"Enda et hemmelig dokument\",\n" +
                                "        \"journalposttype\": \"I\",\n" +
                                "        \"journalstatus\": \"JOURNALFOERT\",\n" +
                                "        \"tema\": \"OPP\",\n" +
                                "        \"datoOpprettet\": \"2024-03-02T12:00:00Z\",\n" +

                                "        \"relevanteDatoer\": [\n" +
                                "          {\n" +
                                "            \"dato\": \"2024-03-01T12:00:00Z\",\n" +
                                "            \"datotype\": \"DATO_REGISTRERT\"\n" +
                                "          }\n" +
                                "        ],\n" +

                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00007777\",\n" +
                                "            \"tittel\": \"VeldigHemmelig.pdf\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"16728392011\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Eldar Vågan\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +
                                "      },\n" +
                                "      {\n" +
                                "        \"journalpostId\": \"429111291\",\n" +
                                "        \"tittel\": \"Svak Postkasse\",\n" +
                                "        \"journalposttype\": \"U\",\n" +
                                "        \"journalstatus\": \"FERDIGSTILT\",\n" +
                                "        \"tema\": \"OPP\",\n" +
                                "        \"datoOpprettet\": \"2024-07-01T12:00:00Z\",\n" +

                                "        \"relevanteDatoer\": [],\n" +

                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00001111\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          },\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00002222\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          },\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00003333\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"77351293720\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Maren Lundby\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +
                                "      },\n" +
                                "      {\n" +
                                "        \"journalpostId\": \"429108246\",\n" +
                                "        \"tittel\": \"Rusten Veikryss\",\n" +
                                "        \"journalposttype\": \"U\",\n" +
                                "        \"journalstatus\": \"UNDER_ARBEID\",\n" +
                                "        \"tema\": \"OPP\",\n" +
                                "        \"datoOpprettet\": \"2021-11-01T12:00:00Z\",\n" +

                                "        \"relevanteDatoer\": [],\n" +

                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00004444\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          },\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00005555\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"11235468820\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Henrik Ibsen\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +
                                "      },\n" +
                                "      {\n" +
                                "        \"journalpostId\": \"428965411\",\n" +
                                "        \"tittel\": \"Rusten Veikryss\",\n" +
                                "        \"journalposttype\": \"N\",\n" +
                                "        \"journalstatus\": \"EKSPEDERT\",\n" +
                                "        \"tema\": \"OPP\",\n" +
                                "        \"datoOpprettet\": \"2023-11-01T12:00:00Z\",\n" +

                                "        \"relevanteDatoer\": [],\n" +

                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00006666\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"45987612091\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Sigrid Undset\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +
                                "      },\n" +

                                "      {\n" +
                                "        \"journalpostId\": \"123456123\",\n" +
                                "        \"tittel\": \"Ekstremt hemmelig dokument\",\n" +
                                "        \"journalposttype\": \"I\",\n" +
                                "        \"journalstatus\": \"MOTTATT\",\n" +
                                "        \"tema\": \"SYK\",\n" +
                                "        \"datoOpprettet\": \"2024-03-02T12:00:00Z\",\n" +

                                "        \"relevanteDatoer\": [\n" +
                                "          {\n" +
                                "            \"dato\": \"2023-05-09T12:00:00Z\",\n" +
                                "            \"datotype\": \"DATO_REGISTRERT\"\n" +
                                "          }\n" +
                                "        ],\n" +

                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00007777\",\n" +
                                "            \"tittel\": \"VeldigHemmelig.pdf\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"16728392011\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Eldar Vågan\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +
                                "      },\n" +

                                "      {\n" +
                                "        \"journalpostId\": \"429101111\",\n" +
                                "        \"tittel\": \"Heisann sveisann\",\n" +
                                "        \"journalposttype\": \"I\",\n" +
                                "        \"journalstatus\": \"JOURNALFOERT\",\n" +
                                "        \"tema\": \"SYM\",\n" +
                                "        \"datoOpprettet\": \"2020-01-01T12:00:00Z\",\n" +

                                "        \"relevanteDatoer\": [],\n" +

                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00007777\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          },\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00008888\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          },\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00009999\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          },\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00010000\",\n" +
                                "            \"tittel\": \"MASKERT_FELT\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"89567423011\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Liv Ullmann\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +
                                "      }\n" +
                                "    ]\n" +
                                "  }\n" +
                                "}")));

        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"brukerId\": {\"id\": \"004\", \"type\": \"FNR\"},\n" +
                        "  \"journalposttyper\": [],\n" +
                        "  \"journalstatuser\": [\"FERDIGSTILT\"],\n" +
                        "  \"tema\": []\n" +
                        "}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Allow all origins
                        .withHeader("Content-Type", "application/json") // Set correct Content-Type
                        .withStatus(200) // Return HTTP 200 OK
                        .withBody("{\n" +
                                "  \"dokumentoversikt\": {\n" +
                                "    \"journalposter\": [\n" +
                                "      {\n" +
                                "        \"journalpostId\": \"666111111\",\n" +
                                "        \"tittel\": \"Hemmelig dokument\",\n" +
                                "        \"journalposttype\": \"U\",\n" +
                                "        \"journalstatus\": \"FERDIGSTILT\",\n" +
                                "        \"tema\": \"OPP\",\n" +
                                "        \"datoOpprettet\": \"2024-03-01T12:00:00Z\",\n" +
                                "        \"relevanteDatoer\": [],\n" +
                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00006666\",\n" +
                                "            \"tittel\": \"Topphemmelig.pdf\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"67298374528\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Raus Trane\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +
                                "      },\n" +
                                "      {\n" +
                                "        \"journalpostId\": \"666222222\",\n" +
                                "        \"tittel\": \"Enda et hemmelig dokument\",\n" +
                                "        \"journalposttype\": \"U\",\n" +
                                "        \"journalstatus\": \"FERDIGSTILT\",\n" +
                                "        \"tema\": \"OPP\",\n" +
                                "        \"datoOpprettet\": \"2024-03-02T12:00:00Z\",\n" +
                                "        \"relevanteDatoer\": [\n" +
                                "          {\n" +
                                "            \"dato\": \"2024-03-01T12:00:00Z\",\n" +
                                "            \"datotype\": \"DATO_REGISTRERT\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"dokumenter\": [\n" +
                                "          {\n" +
                                "            \"dokumentInfoId\": \"00007777\",\n" +
                                "            \"tittel\": \"VeldigHemmelig.pdf\",\n" +
                                "            \"logiskeVedlegg\": [],\n" +
                                "            \"brevkode\": \"NAV 15-04.20\"\n" +
                                "          }\n" +
                                "        ],\n" +
                                "        \"avsenderMottaker\": {\n" +
                                "          \"id\": \"16728392011\",\n" +
                                "          \"type\": \"FNR\",\n" +
                                "          \"navn\": \"Eldar Vågan\",\n" +
                                "          \"land\": \"Norge\"\n" +
                                "        }\n" +
                                "      }\n" +
                                "    ]\n" +
                                "  }\n" +
                                "}")));

        /////////////////////////////////////////////////////////////////////////FILTER STUBS/////////////////////////////////////////////////////////////////////////////////////////////////////


        //INGEN FILTER
        //Mock for søkeresultat "002". Gir response basert på brukerID input fra clienten.
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\"brukerId\": {\"id\": \"002\"}}"
                        , true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002
                                )));

        //FILTER
        //Mock for søkeresultat "002", TIL
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"tema\": [\"TIL\"]\n" +
                                "}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_TIL
                        )));

        //FILTER
        //Mock for søkeresultat "002", TIL, SYM
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"tema\": [\"TIL\", \"SYM\"]\n" +
                                "}", true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_TIL_SYM
                        )));

        //FILTER
        //Mock for søkeresultat "002", FERDIGSTILT
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"FERDIGSTILT\"]\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_FERDIGSTILT
                        )));

        //FILTER
        //Mock for søkeresultat "002", FERDIGSTILT, N
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"FERDIGSTILT\"],\n" +
                                "  \"journalposttyper\": [\"N\"]\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_FERDIGSTILT_N
                        )));

        //FILTER
        //Mock for søkeresultat "002", JOURNALFOERT
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"JOURNALFOERT\"]\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_JOURNALFOERT
                        )));

        //FILTER
        //Mock for søkeresultat "002", JOURNALFOERT_N
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"JOURNALFOERT\"],\n" +
                                "  \"journalposttyper\": [\"N\"]\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_JOURNALFOERT_N
                        )));


        //FILTER
        //Mock for søkeresultat "002", EKSPEDERT
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"EKSPEDERT\"]\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_EKSPEDERT
                        )));

        //FILTER
        //Mock for søkeresultat "002", FERDIGSTILT, JOURNALFOERT
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"FERDIGSTILT\", \"JOURNALFOERT\"]\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_FERDIGSTILT_JOURNALFOERT
                        )));

        //FILTER
        //Mock for søkeresultat "002", FERDIGSTILT, JOURNALFOERT_I
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"FERDIGSTILT\", \"JOURNALFOERT\"],\n" +
                                "  \"journalposttyper\": [\"I\"]\n" +

                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_FERDIGSTILT_JOURNALFOERT_I
                        )));

        //FILTER
        //Mock for søkeresultat "002", FERDIGSTILT, JOURNALFOERT_I_N
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"FERDIGSTILT\", \"JOURNALFOERT\"],\n" +
                                "  \"journalposttyper\": [\"I\",\"N\"]\n" +

                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_FERDIGSTILT_JOURNALFOERT_I_N
                        )));

        //FILTER
        //Mock for søkeresultat "002", FERDIGSTILT, EKSPEDERT
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"FERDIGSTILT\", \"EKSPEDERT\"]\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_FERDIGSTILT_EKSPEDERT
                        )));

        //FILTER
        //Mock for søkeresultat "002", JOURNALFOERT, EKSPEDERT
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"journalstatuser\": [\"JOURNALFOERT\", \"EKSPEDERT\"]\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_JOURNALFOERT_EKSPEDERT
                        )));

        //FILTER
        //Mock for søkeresultat "002", FRA 01.01.22 TIL 31.12.22
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"fraDato\": \"2021-12-31T23:00:00.000Z\",\n" +
                                "  \"tilDato\": \"2022-12-30T23:00:00.000Z\"\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_2022
                        )));


        //FILTER
        //Mock for søkeresultat "002", FRA 01.01.23 TIL 31.12.23
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\n" +
                                "  \"brukerId\": {\"id\": \"002\"},\n" + // Legg merke til kommaet her
                                "  \"fraDato\": \"2022-12-31T23:00:00.000Z\",\n" +
                                "  \"tilDato\": \"2023-12-30T23:00:00.000Z\"\n" +
                                "}", true, true))

                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200) // Returner HTTP 200 OK
                        .withBody(
                                brukerID_002_2023
                        )));


        /////// EXCEPTION HANDLING STUBS///////

        wireMockServer.stubFor(get(urlEqualTo("/mock/error"))
                .willReturn(aResponse()

                        .withStatus(403) 
                        ));




        wireMockServer.stubFor(patch(urlPathMatching("/rest/journalpostapi/v1/journalpost/.*/feilregistrer/settStatusUtgaar"))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withStatus(200)));

        wireMockServer.stubFor(patch(urlPathMatching("/rest/journalpostapi/v1/journalpost/.*/feilregistrer/settStatusAvbryt"))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withStatus(200)));

        wireMockServer.stubFor(patch(urlEqualTo("/rest/test"))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));


        // For å sette av MottattDato
        wireMockServer.stubFor(put(urlPathMatching("/rest/journalpostapi/v1/journalpost/.*"))
                .withRequestBody(matchingJsonPath("$.date", matching("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z")))  // Regex for ISO 8601 format
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*") // Tillat forespørsler fra alle opprinnelser
                        .withHeader("Content-Type", "application/json") // Sett riktig Content-Type for respons
                        .withStatus(200))); // Returner HTTP 200 OK



        //Mock for søkeresultat "400".
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\"brukerId\": {\"id\": \"400\"}}"
                        , true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody(
                                "Ugyldig input. JournalpostId og dokumentInfoId må være tall og variantFormat må være en gyldig kodeverk-verdi som ARKIV eller ORIGINAL. " +
                                        "Journalposten tilhører et ustøttet arkivsaksystem. Arkivsaksystem må være GSAK, PSAK eller NULL (midlertidig journalpost)."
                        )// Returner HTTP 400 OK
                ));

        //Mock for søkeresultat "401".
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\"brukerId\": {\"id\": \"401\"}}"
                        , true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(401)
                        .withBody(
                                "Vi kan ikke autorisere bruker gjennom token eller system som har gitt token er ukjent for saf. " +
                                        "F.eks ugyldig, utgått, manglende OIDC token eller ingen audience hos saf."
                        )
                ));

        //Mock for søkeresultat "403".
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\"brukerId\": {\"id\": \"403\"}}"
                        , true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(403)
                        .withBody(
                                "Vi kan ikke gi tilgang til dokumentet på grunn av sikkerhet eller personvern. " +
                                        "F.eks dokumentet tilhører egen ansatt eller bruker som bor på hemmelig adresse. Eller bruker har ikke tilgang til tema." +
                                        "Referer til dokumentasjon om tilgangskontrollen til saf for mer informasjon. " +
                                        "Tilgang for saksbehandler og system styres gjennom NORG og gruppemedlemskap i AD."
                        )
                ));

        //Mock for søkeresultat "404".
        wireMockServer.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(
                        "{\"brukerId\": {\"id\": \"404\"}}"
                        , true, true))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withHeader("Access-Control-Allow-Origin", "*")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                        .withBody(
                                "Dokumentet ble ikke funnet i fagarkivet."
                        )
                ));

    }






    @PreDestroy
    public void stopWireMockServer() {
        wireMockServer.stop();
    }
}
