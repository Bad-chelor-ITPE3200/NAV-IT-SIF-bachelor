package com.bachelor.vju_vm_apla2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VjuLocalApplication {
    public static void main(String[] args){
        SpringApplication app = new SpringApplication(VjuLocalApplication.class);
        app.setAdditionalProfiles("local");
        app.run(args);
    }
}
