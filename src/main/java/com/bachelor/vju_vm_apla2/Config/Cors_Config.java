package com.bachelor.vju_vm_apla2.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class Cors_Config {
    //Frontend URL
    @Value("${FRONTEND.COMBINED}")
    private String CORS;
    //ouath2 url
   @Value("${mock-oauth2-server.combined}")
   private String OAUTH2Server;
   //wiremock url
   @Value("${wiremock-saf.combined}")
   private String wiremocksaf;

   @Value("{wiremock-dok.combined}")
   private String wiremockdok;
    @Bean
    public WebMvcConfigurer corsconf() {
    //all the different urls that are allowed to talk to the backend
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry reg) {
                reg.addMapping("/**").allowedOrigins(CORS, OAUTH2Server, wiremocksaf, wiremockdok).allowedMethods("*").allowedHeaders("*");

            }

    };
    }
}
