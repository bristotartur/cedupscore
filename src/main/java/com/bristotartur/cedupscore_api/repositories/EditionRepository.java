package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Edition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditionRepository extends JpaRepository<Edition, Long> {

    @Query("SELECT e FROM Edition e WHERE YEAR(e.startDate) = :year")
    Optional<Edition> findByYear(@Param("year") Integer year);

}
