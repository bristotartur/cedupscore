package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Event;
import com.bristotartur.cedupscore_api.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("SELECT e FROM Event e WHERE e.id = :id AND e.type = :type")
    Optional<Event> findEventByIdAndType(@Param("id") Long id, @Param("type") EventType type);

}
