package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.User;
import com.bristotartur.cedupscore_api.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByRole(RoleType role, Pageable pageable);

    Optional<User> findByEmail(String email);

}
