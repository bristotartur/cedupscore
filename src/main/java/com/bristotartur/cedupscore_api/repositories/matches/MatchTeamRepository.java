package com.bristotartur.cedupscore_api.repositories.matches;

import com.bristotartur.cedupscore_api.domain.matches.MatchTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchTeamRepository extends JpaRepository<MatchTeam, Long> {
}
