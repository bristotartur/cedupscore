package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.EventScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventScoreRepository extends JpaRepository<EventScore, Long> {
}
