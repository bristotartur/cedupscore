package com.bristotartur.cedupscore_api.repositories.events;

import com.bristotartur.cedupscore_api.domain.events.Edition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditionRepository extends JpaRepository<Edition, Long> {
}
