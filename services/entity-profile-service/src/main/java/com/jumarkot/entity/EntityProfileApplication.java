package com.jumarkot.entity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class EntityProfileApplication {
    public static void main(String[] args) {
        SpringApplication.run(EntityProfileApplication.class, args);
    }
}
