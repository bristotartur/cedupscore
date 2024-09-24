package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.dtos.request.LoginRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.LoginResponseDto;
import com.bristotartur.cedupscore_api.enums.Patterns;
import com.bristotartur.cedupscore_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto dto, JwtEncoder jwtEncoder) {
        if (!Patterns.validateEmail(dto.email())) {
            throw new BadCredentialsException("Usuário ou senha inválidos.");
        }
        var user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha inválidos."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("Usuário ou senha inválidos.");
        }
        var expiresIn = 300L;
        var token = this.createToken(expiresIn, user.getId().toString(), user.getRole().name(), jwtEncoder);

        return new LoginResponseDto(token, expiresIn);
    }

    private String createToken(Long expiresIn, String userId, String scope, JwtEncoder encoder) {
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("cedupscore-api")
                .subject(userId)
                .claim("scope", scope)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
