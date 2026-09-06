package com.br.application.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("API SistemaJCB - Pecuária de Precisao operando no verde! Acesse /swagger-ui/index.html para visualizar os endpoints.");
    }
}