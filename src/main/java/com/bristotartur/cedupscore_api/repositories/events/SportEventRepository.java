package com.bristotartur.cedupscore_api.repositories.events;

import com.bristotartur.cedupscore_api.domain.events.SportEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface SportEventRepository extends EventRepository<SportEvent> {
}
