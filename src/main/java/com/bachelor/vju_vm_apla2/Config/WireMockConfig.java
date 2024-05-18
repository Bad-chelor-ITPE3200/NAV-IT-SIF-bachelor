package com.bachelor.vju_vm_apla2.Config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Configuration
public class WireMockConfig {
    //setter opp wiremock server og enabler logging
    /* @Beans means that the Spring IOC, which manages specific instances or objects, should handle the method
       wireMockServer() and create a bean out of it. initMethod attribute states what method should be called
       immediately after the bean (an object that defines dependencies without explicitly creating them in the
       same project) is instantiated.  https://www.baeldung.com/spring-bean
       destroyMethod is what should be called right before Spring destroys the bean. */

    @Value("${wiremock-saf.port}")
    public int saf_port;

    @Value("${wiremock-saf.url}")
    public String saf_url;

    @Value("${wiremock-saf.files}")
    public String files;
    @Primary
    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        return new WireMockServer(options()
                .bindAddress(saf_url)
                .port(saf_port)
                .extensions(new StubRespons_Config())
                /* Below is the classpath which is "." meaning current classpath root, where WireMock will look
                   for files such as stubs. */
                .usingFilesUnderClasspath(files) //change this one for not in use with docker //IMPORTANT: CHANGE THIS WHEN NOT CREATING DOCKER IMAGES TO .
                // Below configures a logger with WireMock, which logs info to the console for debugging
                .notifier(new ConsoleNotifier(true)));
}
}