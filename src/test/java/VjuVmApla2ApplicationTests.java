import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = VjuLocalApplication.class, properties = {"discoveryUrl=${mock-oauth2-server.combined}/test/.well-known/openid-configuration"}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableMockOAuth2Server(port = 8082)
class VjuVmApla2ApplicationTests {

    @Autowired
    private MockOAuth2Server server;

    @Value("${discoveryUrl}")
    private String discoveryUrl;
    @Test
    public void serverStartsOnStaticPortAndIsUpdatedInEnv() {
        assertEquals(8082, server.baseUrl().port());
        assertThat(server.wellKnownUrl("test")).hasToString(discoveryUrl);
    }

}
