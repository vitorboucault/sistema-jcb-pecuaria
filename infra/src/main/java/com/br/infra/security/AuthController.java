package com.br.infra.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        var authToken = new UsernamePasswordAuthenticationToken(dto.usuario(), dto.senha());
        authenticationManager.authenticate(authToken);

        String jwt = tokenService.gerarToken(dto.usuario());
        return ResponseEntity.ok(new LoginResponseDTO(jwt, dto.usuario()));
    }

    public record LoginRequestDTO(String usuario, String senha) {}
    public record LoginResponseDTO(String token, String usuario) {}
}