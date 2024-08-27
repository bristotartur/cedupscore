package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.MatchSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetRepository extends JpaRepository<MatchSet, Long> {
}
