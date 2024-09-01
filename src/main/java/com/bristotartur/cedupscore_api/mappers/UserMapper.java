package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.Role;
import com.bristotartur.cedupscore_api.domain.User;
import com.bristotartur.cedupscore_api.dtos.request.RequestUserDto;
import com.bristotartur.cedupscore_api.dtos.response.ResponseUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password")
    @Mapping(target = "roles", source = "roles")
    User toNewUser(RequestUserDto dto, String password, Set<Role> roles);

    @Mapping(target = "password", source = "password")
    @Mapping(target = "roles", source = "roles")
    User toExistingUser(Long id, RequestUserDto dto, String password, Set<Role> roles);

    @Mapping(target = "roles", source = "roles")
    ResponseUserDto toUserResponseDto(User user, List<String> roles);

}
