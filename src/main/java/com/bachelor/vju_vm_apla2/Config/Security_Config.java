package com.bachelor.vju_vm_apla2.Config;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.context.annotation.Configuration;

/* Essentially, the main annotation here for our prod project, is the @EnableJwtTokenValidation, which enables
   JWT validation on incoming requests. So any request that doesn't have a valid JWT token will be rejected */
//todo: might need to reconfigure this to work with a dockerfile, might need to change url only
@Configuration
@EnableJwtTokenValidation
public class Security_Config {

}
