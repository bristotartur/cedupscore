package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Event;
import com.bristotartur.cedupscore_api.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByType(EventType type);

    Page<Event> findByType(EventType type, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id = :id AND e.type = :type")
    Optional<Event> findEventByIdAndType(@Param("id") Long id, @Param("type") EventType type);

}
