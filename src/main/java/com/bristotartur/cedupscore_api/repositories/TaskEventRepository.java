package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.events.TaskEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskEventRepository extends EventRepository<TaskEvent> {
}
