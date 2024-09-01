package com.bristotartur.cedupscore_api.dtos.response;

import java.util.List;

public record ResponseUserDto(Long id, String name, String email, List<String> roles) {

}
