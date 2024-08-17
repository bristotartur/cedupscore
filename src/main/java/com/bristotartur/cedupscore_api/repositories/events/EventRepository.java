package com.bristotartur.cedupscore_api.repositories.events;

import com.bristotartur.cedupscore_api.domain.events.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository<T extends Event> extends JpaRepository<T, Long> {
}
