package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.MatchTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchTeamRepository extends JpaRepository<MatchTeam, Long> {
}
