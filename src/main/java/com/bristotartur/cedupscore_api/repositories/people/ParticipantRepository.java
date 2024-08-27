package com.bristotartur.cedupscore_api.repositories.people;

import com.bristotartur.cedupscore_api.domain.events.Edition;
import com.bristotartur.cedupscore_api.domain.events.Event;
import com.bristotartur.cedupscore_api.domain.people.Participant;
import com.bristotartur.cedupscore_api.domain.people.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("SELECT p FROM Participant p WHERE p.name LIKE %:name%")
    Page<Participant> findByNameLike(@Param("name") String name, Pageable pageable);

    @Query("SELECT er.participant FROM EditionRegistration er WHERE er.edition = :edition")
    Page<Participant> findByEdition(@Param("edition") Edition edition, Pageable pageable);

    @Query("""
        SELECT er.participant FROM EditionRegistration er
        WHERE
            er.team = :team AND er.edition = :edition
    """)
    Page<Participant> findByTeamAndEdition(@Param("team") Team team, @Param("edition") Edition edition, Pageable pageable);

    @Query("SELECT er.participant FROM EventRegistration er WHERE er.event = :event")
    Page<Participant> findByEvent(@Param("event") Event event, Pageable pageable);

    @Query("""
        SELECT er.participant FROM EventRegistration er
        WHERE
            er.team = :team AND er.event = :event
    """)
    Page<Participant> findByTeamAndEvent(@Param("team") Team team, @Param("event") Event event, Pageable pageable);

    @Query("""
        SELECT er.participant FROM EventRegistration er
        WHERE
            er.team = :team AND er.event = :event
    """)
    List<Participant> findByTeamAndEvent(@Param("team") Team team, @Param("event") Event event);

    @Query("SELECT p FROM Participant p WHERE p.cpf = :cpf")
    Optional<Participant> findByCpf(@Param("cpf") String cpf);

}
