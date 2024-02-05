package sifvju.sifvju;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/* We ended up using wiremock-standalone artifact to include all dependencies that WireMock requires to run, packaged
   into a single JAR file, so that the wiremock dependency, not only has wiremock, but also an embedded Jetty server,
   which was causing the server to crash because the latest version of wiremock is only compatible with Spring 3.1,
   which uses Jetty 11 and we are using Spring 3.2, which uses Jetty 12, which wiremock is not compatible with. */

@SpringBootApplication
public class SifVjuApplication {

    private WireMockServer wireMockServer;

    public static void main(String[] args) {
        SpringApplication.run(SifVjuApplication.class, args);
    }

    @PostConstruct
    public void startWireMockServer() {
        wireMockServer = new WireMockServer(8081); // or any other port you wish to use
        wireMockServer.start();

        configureFor("localhost", wireMockServer.port());
        /* Configure your stubs here
        stubFor(get(urlEqualTo("/baeldung")).willReturn(aResponse().withBody("Welcome to Baeldung!")));

        // Now make a call to the /baeldung endpoint
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/baeldung";
        String response = restTemplate.getForObject(url, String.class);

        System.out.println(response);

        */
        String requestJson = "{\"query\": \"query {  journalpost(journalpostId: \\\"453857319\\\")" +
                " {    journalposttype    journalstatus   tema    tittel    journalstatus    dokumenter" +
                " {      dokumentInfoId      tittel    }    avsenderMottaker {      navn    }  }}\"}";


        // Stub for GraphQL query
        stubFor(post(urlEqualTo("/graphql"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(requestJson))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"data\": { \"journalpost\": { \"journalposttype\": \"I\", \"journalstatus\": \"MOTTATT\"," +
                                " \"tema\": \"OMS\", \"tittel\": \"Søknad om utbetaling av omsorgspenger for arbeidstaker\"," +
                                " \"dokumenter\": [ { \"dokumentInfoId\": \"648126654\", \"tittel\": \"Innvilgelse.pdf\" } ]," +
                                " \"avsenderMottaker\": { \"navn\": \"UTYDELIG, SKÅL\" } } } }")));

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/graphql";

        HttpHeaders headers = new HttpHeaders();
        // setContentType() specifies that the request body contains JSON data. Aka this sets the content-type header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Below sets the accept header, which informs what response the client is willing to accept, which is JSON
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        System.out.println(response.getBody());
    }

    @PreDestroy
    public void stopWireMockServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
