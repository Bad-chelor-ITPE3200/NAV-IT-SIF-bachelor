package com.example.navitsifbachelor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class , HibernateJpaAutoConfiguration.class}) //<- kun for testing av backend, fjernes når graph setup er konfigurert
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,})
public class NavItSifBachelorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NavItSifBachelorApplication.class, args);
    }

}
