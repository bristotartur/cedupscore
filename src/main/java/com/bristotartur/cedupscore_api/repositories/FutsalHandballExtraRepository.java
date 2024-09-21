package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.FutsalHandballExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FutsalHandballExtraRepository extends JpaRepository<FutsalHandballExtra, Long> {
}
