package com.bristotartur.cedupscore_api.repositories.matches;

import com.bristotartur.cedupscore_api.domain.matches.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
}
