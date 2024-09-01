package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :name")
    Page<User> findByRole(@Param("name") String roleName, Pageable pageable);

    Optional<User> findByEmail(String email);

}
