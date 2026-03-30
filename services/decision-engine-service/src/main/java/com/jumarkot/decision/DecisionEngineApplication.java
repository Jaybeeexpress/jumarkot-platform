package com.jumarkot.decision;

import com.jumarkot.decision.config.RulesServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RulesServiceProperties.class)
public class DecisionEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(DecisionEngineApplication.class, args);
    }
}
