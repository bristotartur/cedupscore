package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Punishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PunishmentRepository extends JpaRepository<Punishment, Long> {
}
