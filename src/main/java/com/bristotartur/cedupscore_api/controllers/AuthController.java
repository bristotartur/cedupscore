package com.bristotartur.cedupscore_api.controllers;

import com.bristotartur.cedupscore_api.dtos.request.LoginRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.LoginResponseDto;
import com.bristotartur.cedupscore_api.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtEncoder jwtEncoder;
    private final AuthService authService;

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto, jwtEncoder));
    }

}
