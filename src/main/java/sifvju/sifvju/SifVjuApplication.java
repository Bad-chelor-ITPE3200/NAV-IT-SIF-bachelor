package sifvju.sifvju;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootApplication
public class SifVjuApplication {

    private WireMockServer wireMockServer;

    public static void main(String[] args) {
        SpringApplication.run(SifVjuApplication.class, args);
    }
    //
    @PostConstruct
    public void startWireMockServer() {
        wireMockServer = new WireMockServer(8081); // or any other port you wish to use
        wireMockServer.start();

        configureFor("localhost", wireMockServer.port());
        // Configure your stubs here
        stubFor(get(urlEqualTo("/baeldung")).willReturn(aResponse().withBody("Welcome to Baeldung!")));

        // Now make a call to the /baeldung endpoint
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/baeldung";
        String response = restTemplate.getForObject(url, String.class);

        System.out.println(response);
    }

    @PreDestroy
    public void stopWireMockServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
