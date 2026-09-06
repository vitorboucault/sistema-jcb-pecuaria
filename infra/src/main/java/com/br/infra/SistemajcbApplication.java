package com.br.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.br.infra",       // Adaptadores, Jobs e Segurança
        "com.br.application", // Controladores REST
        "com.br.usecase"      // Casos de Uso (onde estao os @Named)
})
@EnableJpaRepositories(basePackages = "com.br.infra.persistence.repository")
@EntityScan(basePackages = "com.br.infra.persistence.entity")
public class SistemajcbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemajcbApplication.class, args);
    }
}
