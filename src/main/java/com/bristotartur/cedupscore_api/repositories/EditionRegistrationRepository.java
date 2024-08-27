package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.EditionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditionRegistrationRepository extends JpaRepository<EditionRegistration, Long> {
}
