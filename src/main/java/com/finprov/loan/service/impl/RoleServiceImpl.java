package com.finprov.loan.service.impl;

import com.finprov.loan.entity.Role;
import com.finprov.loan.repository.RoleRepository;
import com.finprov.loan.service.RoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;

  @Override
  @CacheEvict(value = "roles", allEntries = true)
  public Role createRole(Role role) {
    java.util.Optional<Role> existing = roleRepository.findByNameIncludingDeleted(role.getName());
    if (existing.isPresent()) {
      Role r = existing.get();
      if (Boolean.TRUE.equals(r.getDeleted())) {
        r.setDeleted(false);
        return roleRepository.save(r);
      } else {
        throw new IllegalArgumentException("Role with name " + role.getName() + " already exists");
      }
    }
    return roleRepository.save(role);
  }

  @Override
  @Cacheable(value = "roles")
  public List<Role> getAllRoles() {
    log.info("Fetching roles from database...");
    return roleRepository.findAll();
  }

  @Override
  @CacheEvict(value = "roles", allEntries = true)
  public Role updateRole(Long id, Role role) {
    Role existing =
        roleRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));
    existing.setName(role.getName());
    return roleRepository.save(existing);
  }

  @Override
  @CacheEvict(value = "roles", allEntries = true)
  public void deleteRole(Long id) {
    Role existing =
        roleRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));
    roleRepository.delete(existing);
  }

  @Override
  @CacheEvict(value = "roles", allEntries = true)
  public Role restoreRole(Long id) {
    Role existing =
        roleRepository
            .findByIdIncludingDeleted(id)
            .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));

    existing.setDeleted(false);
    return roleRepository.save(existing);
  }
}
