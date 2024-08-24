package com.bristotartur.cedupscore_api.repositories.registrations;

import com.bristotartur.cedupscore_api.domain.registrations.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
}
