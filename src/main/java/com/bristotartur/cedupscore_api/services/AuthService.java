package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.Role;
import com.bristotartur.cedupscore_api.dtos.request.LoginRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto dto, JwtEncoder jwtEncoder) {
        var user = userService.findUserByEmail(dto.email());

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("Usuário ou senha inválidos.");
        }
        var expiresIn = 300L;
        var userId = user.getId().toString();
        var scopes = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var token = this.createToken(expiresIn, userId, scopes, jwtEncoder);

        return new LoginResponseDto(token, expiresIn);
    }

    private String createToken(Long expiresIn, String userId, String scopes, JwtEncoder encoder) {
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("cedupscore-api")
                .subject(userId)
                .claim("scope", scopes)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
