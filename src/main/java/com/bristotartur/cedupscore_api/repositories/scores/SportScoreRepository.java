package com.bristotartur.cedupscore_api.repositories.scores;

import com.bristotartur.cedupscore_api.domain.scores.SportScore;
import org.springframework.stereotype.Repository;

@Repository
public interface SportScoreRepository extends ScoreRepository<SportScore> {
}
