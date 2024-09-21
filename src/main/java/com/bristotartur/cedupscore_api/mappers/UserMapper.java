package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.User;
import com.bristotartur.cedupscore_api.dtos.request.RequestUserDto;
import com.bristotartur.cedupscore_api.dtos.response.ResponseUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password")
    User toNewUser(RequestUserDto dto, String password);

    @Mapping(target = "password", source = "password")
    User toExistingUser(Long id, RequestUserDto dto, String password);

    ResponseUserDto toUserResponseDto(User user);

}
