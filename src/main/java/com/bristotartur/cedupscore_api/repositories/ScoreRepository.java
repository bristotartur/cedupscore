package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository<T extends Score> extends JpaRepository<T, Long> {
}
