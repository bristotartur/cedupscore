package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.events.TeamScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamScoreRepository extends JpaRepository<TeamScore, Long> {
}
