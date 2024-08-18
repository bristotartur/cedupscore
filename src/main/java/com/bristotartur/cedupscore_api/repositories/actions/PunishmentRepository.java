package com.bristotartur.cedupscore_api.repositories.actions;

import com.bristotartur.cedupscore_api.domain.actions.Punishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PunishmentRepository extends JpaRepository<Punishment, Long> {
}
