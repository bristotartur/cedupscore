package com.bristotartur.cedupscore_api.mappers;

import com.bristotartur.cedupscore_api.domain.Edition;
import com.bristotartur.cedupscore_api.dtos.response.EditionResponseDto;
import com.bristotartur.cedupscore_api.dtos.response.ScoreResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EditionMapper {

    @Mapping(target = "id", ignore = true)
    Edition toNewEdition(Edition edition);

    @Mapping(target = "teamScores", source = "scoreResponseDtos")
    EditionResponseDto toEditionResponseDto(Edition edition, List<ScoreResponseDto> scoreResponseDtos);

}
