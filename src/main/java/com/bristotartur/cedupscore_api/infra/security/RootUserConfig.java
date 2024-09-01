package com.bristotartur.cedupscore_api.infra.security;

import com.bristotartur.cedupscore_api.dtos.request.RequestUserDto;
import com.bristotartur.cedupscore_api.enums.RoleType;
import com.bristotartur.cedupscore_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Transactional
@Profile("!prod")
public class RootUserConfig implements CommandLineRunner {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        var roles = Set.of(RoleType.SUPER_ADMIN, RoleType.EVENT_ADMIN);

        userService.signupUser(
                new RequestUserDto("root", "root@gmail.com", "1234", roles)
        );
    }

}
