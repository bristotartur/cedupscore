package com.bristotartur.cedupscore_api.controllers;

import com.bristotartur.cedupscore_api.dtos.request.RequestUserDto;
import com.bristotartur.cedupscore_api.dtos.response.ResponseUserDto;
import com.bristotartur.cedupscore_api.enums.RoleType;
import com.bristotartur.cedupscore_api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Transactional
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    public ResponseEntity<Page<ResponseUserDto>> listAllUsers(Pageable pageable) {
        var users = userService.findAllUsers(pageable);
        var dtos = users.getContent()
                .stream()
                .map(userService::createUserResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, users.getSize()));
    }

    @GetMapping(path = "/list-by")
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    public ResponseEntity<Page<ResponseUserDto>> listByRole(@RequestParam("role") RoleType role,
                                                            Pageable pageable) {

        var users = userService.findUsersByRole(role, pageable);
        var dtos = users.getContent()
                .stream()
                .map(userService::createUserResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, users.getSize()));
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    public ResponseEntity<ResponseUserDto> findUserById(@PathVariable Long id) {
        var user = userService.findUserById(id);
        return ResponseEntity.ok().body(userService.createUserResponseDto(user));
    }

    @GetMapping(path = "/find-by")
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    public ResponseEntity<ResponseUserDto> findUserByEmail(@RequestParam("email") String email) {
        var user = userService.findUserByEmail(email);
        return ResponseEntity.ok().body(userService.createUserResponseDto(user));
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<ResponseUserDto> signupUser(@RequestBody @Valid RequestUserDto requestDto) {
        var user = userService.signupUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUserResponseDto(user));
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    public ResponseEntity<ResponseUserDto> replaceUser(@PathVariable Long id,
                                                       @RequestBody @Valid RequestUserDto requestDto) {

        var user = userService.replaceUser(id, requestDto);
        return ResponseEntity.ok().body(userService.createUserResponseDto(user));
    }

}
