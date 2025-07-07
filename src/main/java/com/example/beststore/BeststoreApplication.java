package com.example.beststore;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Date;

@EnableJpaAuditing
@SpringBootApplication
public class BeststoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeststoreApplication.class, args);
    }

}
