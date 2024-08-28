package com.bristotartur.cedupscore_api.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleType {
    SUPER_ADMIN(1L),
    EVENT_ADMIN(2L);

    public final Long id;
}
