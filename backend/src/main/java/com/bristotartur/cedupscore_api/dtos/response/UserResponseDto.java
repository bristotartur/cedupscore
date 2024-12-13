package com.bristotartur.cedupscore_api.dtos.response;

import com.bristotartur.cedupscore_api.enums.RoleType;

public record UserResponseDto(Long id, String name, String email, RoleType role) {
}
