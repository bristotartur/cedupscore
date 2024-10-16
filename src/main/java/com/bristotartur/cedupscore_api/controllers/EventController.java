package com.bristotartur.cedupscore_api.controllers;

import com.bristotartur.cedupscore_api.dtos.request.EventRequestDto;
import com.bristotartur.cedupscore_api.dtos.request.EventScoreRequestDto;
import com.bristotartur.cedupscore_api.dtos.response.EventResponseDto;
import com.bristotartur.cedupscore_api.enums.EventType;
import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> listEventsByType(@RequestParam(value = "type", required = false) String type,
                                                                   Pageable pageable) {
        var events = (type != null)
                ? eventService.findAllEvents(EventType.findEventTypeLike(type), pageable)
                : eventService.findAllEvents(pageable);
        var dtos = events.getContent()
                .stream()
                .map(eventService::createEventResponseDto)
                .toList();

        return ResponseEntity.ok().body(new PageImpl<>(dtos, pageable, events.getTotalElements()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<EventResponseDto> findEventById(@RequestParam(value = "type", required = false) String type,
                                                          @PathVariable Long id) {
        var event = (type != null)
                ? eventService.findEventById(id, EventType.findEventTypeLike(type))
                : eventService.findEventById(id);

        return ResponseEntity.ok().body(eventService.createEventResponseDto(event));
    }

    @PostMapping
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<EventResponseDto> saveEvent(@RequestBody EventRequestDto dto) {
        var event = eventService.saveEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEventResponseDto(event));
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<EventResponseDto> replaceEvent(@PathVariable Long id,
                                                         @RequestBody EventRequestDto dto) {
        var event = eventService.replaceEvent(id, dto);
        return ResponseEntity.ok().body(eventService.createEventResponseDto(event));
    }

    @PatchMapping(path = "/{id}/update")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<EventResponseDto> updateEventStatus(@PathVariable Long id,
                                                              @RequestParam("status") String status) {
        var event = eventService.updateEventStatus(id, Status.findStatusLike(status));
        return ResponseEntity.ok().body(eventService.createEventResponseDto(event));
    }

    @PatchMapping(path = "/{id}/close")
    @PreAuthorize(
            "hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EVENT_ADMIN')"
    )
    public ResponseEntity<EventResponseDto> closeEvent(@PathVariable Long id,
                                                       @RequestBody Set<EventScoreRequestDto> dtos) {
        var event = eventService.closeEvent(id, dtos);
        return ResponseEntity.ok().body(eventService.createEventResponseDto(event));
    }

}
