package com.bristotartur.cedupscore_api.controllers;

import com.bristotartur.cedupscore_api.dtos.request.ParticipantRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantResponseDto;
import com.bristotartur.cedupscore_api.services.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/participants")
@RequiredArgsConstructor
@Transactional
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping
    public ResponseEntity<Page<ParticipantResponseDto>> listAllParticipants(Pageable pageable) {
        var participants = participantService.findAllParticipants(pageable);
        var dtos = participants.getContent()
                .stream()
                .map(participantService::createParticipantResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, participants.getSize()));
    }

    @GetMapping(path = "/list-by")
    public ResponseEntity<Page<ParticipantResponseDto>> listParticipantsByName(@RequestParam("name") String name,
                                                                               Pageable pageable) {
        var participants = participantService.findParticipantsByName(name, pageable);
        var dtos = participants.getContent()
                .stream()
                .map(participantService::createParticipantResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, participants.getSize()));
    }

    @GetMapping(path = "/from-edition/{editionId}")
    public ResponseEntity<Page<ParticipantResponseDto>> listParticipantsFromEdition(@PathVariable Long editionId,
                                                                                    Pageable pageable) {
        var participants = participantService.findParticipantsFromEdition(editionId, pageable);
        var dtos = participants.getContent()
                .stream()
                .map(participantService::createParticipantResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, participants.getSize()));
    }

    @GetMapping(path = "/from-team/{teamId}/in-edition/{editionId}")
    public ResponseEntity<Page<ParticipantResponseDto>> listParticipantsFromTeam(@PathVariable Long teamId,
                                                                                 @PathVariable Long editionId,
                                                                                 Pageable pageable) {
        var participants = participantService.findParticipantsFromTeam(teamId, editionId, pageable);
        var dtos = participants.getContent()
                .stream()
                .map(participantService::createParticipantResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, participants.getSize()));
    }

    @GetMapping(path = "/from-event/{eventId}")
    public ResponseEntity<Page<ParticipantResponseDto>> listParticipantsFromEvent(@PathVariable Long eventId,
                                                                                  Pageable pageable) {
        var participants = participantService.findParticipantsFromEvent(eventId, pageable);
        var dtos = participants.getContent()
                .stream()
                .map(participantService::createParticipantResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, participants.getSize()));
    }

    @GetMapping(path = "/from-team/{teamId}/in-event/{eventId}")
    public ResponseEntity<Page<ParticipantResponseDto>> listParticipantsFromEventByTeam(@PathVariable Long teamId,
                                                                                        @PathVariable Long eventId,
                                                                                        Pageable pageable) {
        var participants = participantService.findParticipantsFromEventByTeam(teamId, eventId, pageable);
        var dtos = participants.getContent()
                .stream()
                .map(participantService::createParticipantResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, participants.getSize()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ParticipantResponseDto> findParticipantById(@PathVariable Long id) {
        var participant = participantService.findParticipantById(id);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant));
    }

    @GetMapping(path = "/find")
    public ResponseEntity<ParticipantResponseDto> findParticipantByCpf(@RequestParam("cpf") String cpf) {
        var participant = participantService.findParticipantByCpf(cpf);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant));
    }

    @PostMapping
    public ResponseEntity<ParticipantResponseDto> saveParticipant(@RequestBody @Valid ParticipantRequestDto requestDto) {
        var participant = participantService.saveParticipant(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.createParticipantResponseDto(participant));
    }

    @PostMapping(path = "/{id}/register-in-edition/{editionId}")
    public ResponseEntity<ParticipantResponseDto> registerInEdition(@PathVariable Long id,
                                                                    @PathVariable Long editionId,
                                                                    @RequestParam("team") Long teamId) {
        var participant = participantService.registerParticipantInEdition(id, editionId, teamId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.createParticipantResponseDto(participant));
    }

    @PostMapping(path = "/{id}/register-in-event/{eventId}")
    public ResponseEntity<ParticipantResponseDto> registerInEvent(@PathVariable Long id,
                                                                  @PathVariable Long eventId,
                                                                  @RequestParam("team") Long teamId) {
        var participant = participantService.registerParticipantInEvent(id, eventId, teamId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.createParticipantResponseDto(participant));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/remove-edition-registration/{registrationId}")
    public ResponseEntity<Void> deleteEditionRegistration(@PathVariable Long id, @PathVariable Long registrationId) {
        participantService.deleteEditionRegistration(id, registrationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/remove-event-registration/{registrationId}")
    public ResponseEntity<Void> deleteEventRegistration(@PathVariable Long id, @PathVariable Long registrationId) {
        participantService.deleteEventRegistration(id, registrationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ParticipantResponseDto> replaceParticipant(@PathVariable Long id,
                                                                     @RequestBody @Valid ParticipantRequestDto requestDto) {
        var participant = participantService.replaceParticipant(id, requestDto);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant));
    }

    @PatchMapping(path = "/{id}/set")
    public ResponseEntity<ParticipantResponseDto> setParticipantActive(@PathVariable Long id,
                                                                       @RequestParam("is-active") Boolean isActive) {
        var participant = participantService.setParticipantActive(id, isActive);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant));
    }

}
