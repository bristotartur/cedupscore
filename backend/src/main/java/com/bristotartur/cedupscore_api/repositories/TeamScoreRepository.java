package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Edition;
import com.bristotartur.cedupscore_api.domain.Team;
import com.bristotartur.cedupscore_api.domain.TeamScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamScoreRepository extends JpaRepository<TeamScore, Long> {

    List<TeamScore> findAllByEdition(Edition edition);

    List<TeamScore> findAllByTeam(Team team);

}
