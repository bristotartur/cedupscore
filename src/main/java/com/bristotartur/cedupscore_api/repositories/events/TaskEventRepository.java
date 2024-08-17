package com.bristotartur.cedupscore_api.repositories.events;

import com.bristotartur.cedupscore_api.domain.events.TaskEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskEventRepository extends EventRepository<TaskEvent> {
}
