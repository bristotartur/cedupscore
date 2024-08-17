package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.events.TaskScore;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskScoreRepository extends ScoreRepository<TaskScore> {
}
