package com.bristotartur.cedupscore_api.repositories.people;

import com.bristotartur.cedupscore_api.domain.people.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
