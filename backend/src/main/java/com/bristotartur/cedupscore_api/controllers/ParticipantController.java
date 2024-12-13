package com.bristotartur.cedupscore_api.controllers;

import com.bristotartur.cedupscore_api.domain.Participant;
import com.bristotartur.cedupscore_api.dtos.request.EventRegistrationRequestDto;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantCSVDto;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantFilterDto;
import com.bristotartur.cedupscore_api.dtos.request.ParticipantRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantInactivationReport;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantRegistrationReport;
import com.bristotartur.cedupscore_api.dtos.response.ParticipantResponseDto;
import com.bristotartur.cedupscore_api.services.ParticipantCSVService;
import com.bristotartur.cedupscore_api.services.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/participants")
@RequiredArgsConstructor
@Transactional
public class ParticipantController {

    private final ParticipantService participantService;
    private final ParticipantCSVService participantCSVService;

    @GetMapping
    public ResponseEntity<Page<ParticipantResponseDto>> listAllParticipants(@ModelAttribute ParticipantFilterDto filter,
                                                                            @RequestParam(required = false, name = "not-in-event") Long notInEvent,
                                                                            Pageable pageable) {
        var updatedFilter = (notInEvent != null) ? filter.withUpdatedNotInEvent(notInEvent) : filter;

        var participants = participantService.findAllParticipants(updatedFilter, pageable);
        var dtos = this.generateParticipantResponseDtoList(participants.getContent(), updatedFilter);

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, participants.getTotalElements()));
    }

    @PostMapping(path = "/exclude-ids")
    public ResponseEntity<Page<ParticipantResponseDto>> listAllParticipantsExcludingIds(@ModelAttribute ParticipantFilterDto filter,
                                                                                        @RequestParam(required = false, name = "not-in-event") Long notInEvent,
                                                                                        @RequestBody List<Long> excludeIds,
                                                                                        Pageable pageable) {
        var updatedFilter = (notInEvent != null) ? filter.withUpdatedNotInEvent(notInEvent) : filter;

        var participants = participantService.findAllParticipants(updatedFilter, excludeIds, pageable);
        var dtos = this.generateParticipantResponseDtoList(participants.getContent(), updatedFilter);

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, participants.getTotalElements()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ParticipantResponseDto> findParticipantById(@PathVariable Long id) {
        var participant = participantService.findParticipantById(id);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant, false));
    }
    
    @GetMapping(path = "/{id}/for-update")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<ParticipantResponseDto> findParticipantForUpdate(@PathVariable Long id) {
        var participant = participantService.findParticipantById(id);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant, true));
    }

    @GetMapping(path = "/find")
    public ResponseEntity<ParticipantResponseDto> findParticipantByCpf(@RequestParam("cpf") String cpf) {
        var participant = participantService.findParticipantByCpf(cpf);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant, false));
    }

    @PostMapping
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<ParticipantResponseDto> saveParticipant(@RequestBody @Valid ParticipantRequestDto requestDto) {
        var participant = participantService.saveParticipant(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.createParticipantResponseDto(participant, false));
    }

    @PostMapping(path = "upload/registration-csv", consumes = {"multipart/form-data"})
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<ParticipantRegistrationReport> uploadParticipantsRegistrationCSVFile(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().equals("text/csv")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }
        return ResponseEntity.ok(participantCSVService.handleParticipantsRegistrationCSVFile(file));
    }

    @PostMapping(path = "upload/inactivation-csv", consumes = {"multipart/form-data"})
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<ParticipantInactivationReport> uploadParticipantsInactivationCSVFile(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().equals("text/csv")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }
        return ResponseEntity.ok(participantCSVService.handleParticipantsInactivationCSVFile(file));
    }

    @PostMapping(path = "generate/csv")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<byte[]> generateParticipantsCSV(@RequestParam("type") String type,
                                                          @RequestBody List<ParticipantCSVDto> dtos) {
        var csvBytes = participantCSVService.generateParticipantsCSV(type, dtos);
        var headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "participantes-com-problemas.csv");

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}/register-in-edition/{editionId}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<ParticipantResponseDto> registerInEdition(@PathVariable Long id,
                                                                    @PathVariable Long editionId,
                                                                    @RequestParam("team") Long teamId) {
        var participant = participantService.findParticipantById(id);
        var registeredParticipant = participantService.registerParticipantInEdition(participant, editionId, teamId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.createParticipantResponseDto(registeredParticipant, false));
    }

    @PostMapping(path = "/{id}/register-in-event/{eventId}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<ParticipantResponseDto> registerInEvent(@PathVariable Long id,
                                                                  @PathVariable Long eventId,
                                                                  @RequestParam("team") Long teamId) {
        var participant = participantService.findParticipantById(id);
        var registeredParticipant = participantService.registerParticipantInEvent(participant, eventId, teamId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.createParticipantResponseDto(registeredParticipant, false));
    }

    @PostMapping(path = "/register-in-event/{eventId}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<List<ParticipantResponseDto>> registerAllInEvent(@PathVariable Long eventId,
                                                                           @RequestBody @Valid List<EventRegistrationRequestDto> requestDtos) {
        var participants = participantService.registerAllParticipantsInEvent(requestDtos, eventId);
        var dtos = participants
                .stream()
                .map(participant -> participantService.createParticipantResponseDto(participant, false))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/remove-edition-registration/{registrationId}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<Void> deleteEditionRegistration(@PathVariable Long id, @PathVariable Long registrationId) {
        participantService.deleteEditionRegistration(id, registrationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/remove-event-registration/{registrationId}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<Void> deleteEventRegistration(@PathVariable Long id, @PathVariable Long registrationId) {
        participantService.deleteEventRegistration(id, registrationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/remove-event-registrations")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<Void> deleteAllEventRegistrations(@RequestParam("event") Long eventId,
                                                            @RequestBody List<Long> registrationsIds) {
        participantService.deleteAllEventRegistrationsById(eventId, registrationsIds);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<ParticipantResponseDto> replaceParticipant(@PathVariable Long id,
                                                                     @RequestBody @Valid ParticipantRequestDto requestDto) {
        var participant = participantService.replaceParticipant(id, requestDto);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant, false));
    }

    @PatchMapping(path = "/{id}/set-status")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITION_ADMIN')"
    )
    public ResponseEntity<ParticipantResponseDto> setParticipantActive(@PathVariable Long id,
                                                                       @RequestParam("is-active") Boolean status) {
        var participant = participantService.setParticipantStatus(id, status);
        return ResponseEntity.ok().body(participantService.createParticipantResponseDto(participant, false));
    }

    private List<ParticipantResponseDto> generateParticipantResponseDtoList(List<Participant> participants, ParticipantFilterDto filter) {

        return participants.stream()
                .map(participant -> {
                    var eventId = filter.event();
                    if (eventId != null) {
                        return participant.getEventRegistrations().stream()
                                .filter(registration -> registration.getEvent().getId().equals(eventId))
                                .findFirst()
                                .map(registration -> participantService.createParticipantResponseDto(participant, registration, false))
                                .orElseGet(() -> participantService.createParticipantResponseDto(participant, false));
                    }
                    return participantService.createParticipantResponseDto(participant, false);
                })
                .toList();
    }

}
