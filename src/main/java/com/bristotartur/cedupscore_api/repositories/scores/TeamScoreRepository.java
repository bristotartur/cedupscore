package com.bristotartur.cedupscore_api.repositories.scores;

import com.bristotartur.cedupscore_api.domain.events.Edition;
import com.bristotartur.cedupscore_api.domain.people.Team;
import com.bristotartur.cedupscore_api.domain.scores.TeamScore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamScoreRepository extends ScoreRepository<TeamScore> {

    List<TeamScore> findAllByEdition(Edition edition);

    List<TeamScore> findAllByTeam(Team team);

}
