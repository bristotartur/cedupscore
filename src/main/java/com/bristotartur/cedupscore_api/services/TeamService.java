package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.Team;
import com.bristotartur.cedupscore_api.dtos.request.TeamRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.TeamResponseDto;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.exceptions.ConflictException;
import com.bristotartur.cedupscore_api.exceptions.NotFoundException;
import com.bristotartur.cedupscore_api.exceptions.UnprocessableEntityException;
import com.bristotartur.cedupscore_api.mappers.TeamMapper;
import com.bristotartur.cedupscore_api.repositories.TeamRepository;
import com.bristotartur.cedupscore_api.repositories.TeamScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final TeamScoreRepository teamScoreRepository;

    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }

    public List<Team> findAllTeamsById(List<Long> ids) {
        return teamRepository.findAllById(ids);
    }

    public List<Team> findAllTeamsById(Set<Long> ids) {
        return teamRepository.findAllById(ids);
    }

    public List<Team> findAllActiveTeams() {

        return teamRepository.findAll()
                .stream()
                .filter(Team::getIsActive)
                .toList();
    }

    public Team findTeamById(Long id) {

        return teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipe não encontrada."));
    }

    public TeamResponseDto createTeamResponseDto(Team team) {
        return teamMapper.toTeamResponseDto(team);
    }

    public Team saveTeam(TeamRequestDto dto) {

        teamRepository.findByName(dto.name()).ifPresent(team -> {
            throw new ConflictException("O nome '%s' já está em uso.".formatted(team.getName()));
        });
        teamRepository.findByLogoUrl(dto.logoUrl()).ifPresent(team -> {
            throw new ConflictException("A logo '%s' já está em uso.".formatted(team.getLogoUrl()));
        });
        return teamRepository.save(teamMapper.toNewTeam(dto));
    }

    public void deleteTeam(Long id) {
        var team = this.findTeamById(id);

        if (!teamScoreRepository.findAllByTeam(team).isEmpty()) {
            throw new UnprocessableEntityException("A equipe não pode ser removida.");
        }
        teamRepository.delete(team);
    }

    public Team replaceTeam(Long id, TeamRequestDto dto) {
        var team = this.findTeamById(id);

        if (!team.getName().equals(dto.name())) {
            teamRepository.findByName(dto.name()).ifPresent(t -> {
                throw new ConflictException("O nome '%s' já está em uso.".formatted(t.getName()));
            });
        }
        if (!team.getLogoUrl().equals(dto.logoUrl())) {
            teamRepository.findByLogoUrl(dto.logoUrl()).ifPresent(t -> {
                throw new ConflictException("A logo '%s' já está em uso.".formatted(t.getLogoUrl()));
            });
        }
        return teamRepository.save(teamMapper.toExistingTeam(id, dto, team.getIsActive()));
    }

    public Team setTeamActive(Long id, boolean isActive) {
        var team = this.findTeamById(id);

        if (isActive == team.getIsActive()) return team;

        if (!isActive) {
            teamScoreRepository.findAllByTeam(team)
                    .stream()
                    .map(registration -> registration.getEdition().getStatus())
                    .filter(status -> !status.equals(Status.ENDED) && !status.equals(Status.CANCELED))
                    .findFirst()
                    .ifPresent(status -> {
                        throw new ConflictException("A equipe não pode ser desativada.");
                    });
        }
        team.setIsActive(isActive);
        return teamRepository.save(team);
    }

}
