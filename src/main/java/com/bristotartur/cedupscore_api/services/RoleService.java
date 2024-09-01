package com.bristotartur.cedupscore_api.services;

import com.bristotartur.cedupscore_api.domain.Role;
import com.bristotartur.cedupscore_api.exceptions.NotFoundException;
import com.bristotartur.cedupscore_api.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public Role findRoleByName(String name) {

        return roleRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Role não encontrada."));
    }

}
