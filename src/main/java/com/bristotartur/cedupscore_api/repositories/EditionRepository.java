package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Edition;
import com.bristotartur.cedupscore_api.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EditionRepository extends JpaRepository<Edition, Long> {

    @Query("SELECT e FROM Edition e ORDER BY e.startDate DESC")
    List<Edition> findAllDescending();

    @Query("SELECT e FROM Edition e WHERE YEAR(e.startDate) = :year")
    Optional<Edition> findByYear(@Param("year") Integer year);

    List<Edition> findByStatus(@Param("status") Status status);

    @Query("SELECT e FROM Edition e WHERE e.status NOT IN :statuses")
    List<Edition> findByStatusNotIn(@Param("statuses") List<Status> statuses);

}
