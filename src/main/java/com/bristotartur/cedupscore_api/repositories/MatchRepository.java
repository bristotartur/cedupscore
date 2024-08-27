package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
}
