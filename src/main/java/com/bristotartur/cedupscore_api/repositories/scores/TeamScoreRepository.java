package com.bristotartur.cedupscore_api.repositories.scores;

import com.bristotartur.cedupscore_api.domain.scores.TeamScore;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamScoreRepository extends ScoreRepository<TeamScore> {
}
