package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
}
