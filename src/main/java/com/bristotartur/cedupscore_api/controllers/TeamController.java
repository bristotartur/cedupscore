package com.bristotartur.cedupscore_api.controllers;

import com.bristotartur.cedupscore_api.dtos.request.TeamRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.TeamResponseDto;
import com.bristotartur.cedupscore_api.services.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Transactional
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamResponseDto>> listAllTeams() {
        var teams = teamService.findAllTeams();
        var dtos = teams.stream()
                .map(teamService::createTeamResponseDto)
                .toList();

        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TeamResponseDto> findTeamById(@PathVariable Long id) {
        var team = teamService.findTeamById(id);
        return ResponseEntity.ok().body(teamService.createTeamResponseDto(team));
    }

    @PostMapping
    public ResponseEntity<TeamResponseDto> saveTeam(@RequestBody @Valid TeamRequestDto requestDto) {
        var team = teamService.saveTeam(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeamResponseDto(team));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<TeamResponseDto> replaceTeam(@PathVariable Long id,
                                                       @RequestBody @Valid TeamRequestDto requestDto) {

        var team = teamService.replaceTeam(id, requestDto);
        return ResponseEntity.ok().body(teamService.createTeamResponseDto(team));
    }

    @PatchMapping(path = "/{id}/set")
    public ResponseEntity<TeamResponseDto> setTeamActive(@PathVariable Long id,
                                                         @RequestParam("is-active") Boolean isActive) {

        var team = teamService.setTeamActive(id, isActive);
        return ResponseEntity.ok().body(teamService.createTeamResponseDto(team));
    }

}
