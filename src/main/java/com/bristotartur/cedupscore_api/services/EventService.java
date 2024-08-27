package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.events.Event;
import com.bristotartur.cedupscore_api.exceptions.NotFoundException;
import com.bristotartur.cedupscore_api.repositories.events.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public Event findEventById(Long id) {

        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado."));
    }

}
