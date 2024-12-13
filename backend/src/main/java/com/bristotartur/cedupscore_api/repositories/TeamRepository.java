package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    Optional<Team> findByLogoUrl(String logoUrl);

}
