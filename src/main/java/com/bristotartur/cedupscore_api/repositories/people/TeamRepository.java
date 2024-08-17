package com.bristotartur.cedupscore_api.repositories.people;

import com.bristotartur.cedupscore_api.domain.people.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
