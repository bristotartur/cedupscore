package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.User;
import com.bristotartur.cedupscore_api.dtos.request.UserRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password")
    User toNewUser(UserRequestDto dto, String password);

    @Mapping(target = "password", source = "password")
    User toExistingUser(Long id, UserRequestDto dto, String password);

    UserResponseDto toUserResponseDto(User user);

}
