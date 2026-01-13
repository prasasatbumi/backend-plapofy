package com.finprov.loan.service.impl;

import com.finprov.loan.dto.UserDto;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.UserRepository;
import com.finprov.loan.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final com.finprov.loan.repository.RoleRepository roleRepository;
  private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
  private final com.finprov.loan.repository.BranchRepository branchRepository;

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @org.springframework.transaction.annotation.Transactional
  public User createUser(User user) {
    try {
      log.info("Creating user: {} with roles: {} and branches: {}",
          user.getUsername(),
          user.getRoles() != null ? user.getRoles().size() : "null",
          user.getBranches() != null ? user.getBranches().size() : "null");

      user.setId(null);

      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
      } else {
        log.warn("User {} created with empty password", user.getUsername());
        user.setPassword(passwordEncoder.encode("default123")); // Fallback if somehow null
      }

      if (user.getRoles() != null && !user.getRoles().isEmpty()) {
        log.info("Processing roles: {}", user.getRoles());
        java.util.Set<com.finprov.loan.entity.Role> managedRoles = user.getRoles().stream()
            .map(r -> {
              log.info("Looking up role: {}", r.getName());
              return roleRepository.findByName(r.getName())
                  .orElseThrow(() -> new IllegalArgumentException("Role not found: " + r.getName()));
            })
            .collect(java.util.stream.Collectors.toSet());
        user.setRoles(managedRoles);
      } else {
        log.info("No roles provided, using default NASABAH");
        com.finprov.loan.entity.Role defaultRole = roleRepository.findByName("NASABAH")
            .orElseThrow(() -> new IllegalStateException("Default role NASABAH not found"));
        user.setRoles(java.util.Set.of(defaultRole));
      }

      // Handle branches
      if (user.getBranches() != null && !user.getBranches().isEmpty()) {
        log.info("Processing branches: {}", user.getBranches());
        java.util.Set<com.finprov.loan.entity.Branch> managedBranches = user.getBranches().stream()
            .map(b -> {
              return branchRepository.findById(b.getId())
                  .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + b.getId()));
            })
            .collect(java.util.stream.Collectors.toSet());
        user.setBranches(managedBranches);
      }
      return userRepository.save(user);
    } catch (Exception e) {
      log.error("CRITICAL ERROR CREATING USER: " + e.getMessage());
      e.printStackTrace(); // Print to stdout as well
      throw new RuntimeException("Creation failed: " + e.getMessage(), e);
    }
  }

  // @Cacheable(value = "users", key = "#branchId != null ? #branchId : 'all'")
  public List<UserDto> getAllUsers(Long branchId) {
    log.info("DEBUG: getAllUsers called with branchId: {}", branchId);
    List<User> users;
    if (branchId != null) {
      log.info("DEBUG: Filtering by branchId: {}", branchId);
      users = userRepository.findByUserBranches_Branch_IdAndDeletedFalse(branchId);
      log.info("DEBUG: Found {} users for branch {}", users.size(), branchId);
    } else {
      log.info("DEBUG: Fetching ALL users");
      users = userRepository.findAll();
    }

    return users.stream()
        .map(
            u -> UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .isActive(u.getIsActive())
                .roles(u.getRoles().stream()
                    .map(r -> com.finprov.loan.dto.RoleDto.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .build())
                    .collect(java.util.stream.Collectors.toSet()))
                .branches(u.getBranches().stream()
                    .map(b -> com.finprov.loan.dto.BranchDto.builder()
                        .id(b.getId())
                        .code(b.getCode())
                        .name(b.getName())
                        .build())
                    .collect(java.util.stream.Collectors.toSet()))
                .build())
        .collect(Collectors.toList());
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  public void deleteUser(Long id) {
    User user = userRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    userRepository.delete(user);
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  public void updateStatus(Long id, Boolean isActive) {
    User user = userRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    user.setIsActive(isActive);
    userRepository.save(user);
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @org.springframework.transaction.annotation.Transactional
  public User updateUser(Long id, User userDetails) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

    if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
      user.setUsername(userDetails.getUsername());
    }
    if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
      user.setEmail(userDetails.getEmail());
    }
    // Update password if provided/needed (optional, skipping for now as request
    // didn't specify password reset)

    if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
      java.util.Set<com.finprov.loan.entity.Role> managedRoles = userDetails.getRoles().stream()
          .map(r -> {
            return roleRepository.findByName(r.getName())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + r.getName()));
          })
          .collect(java.util.stream.Collectors.toSet());
      user.setRoles(managedRoles);
    }

    return userRepository.save(user);
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @org.springframework.transaction.annotation.Transactional
  public User assignBranches(Long userId, List<Long> branchIds) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    if (branchIds == null || branchIds.isEmpty()) {
      user.getUserBranches().clear();
    } else {
      // Map existing branches to preserve creation date
      java.util.Map<Long, com.finprov.loan.entity.UserBranch> existingMap = user.getUserBranches().stream()
          .collect(java.util.stream.Collectors.toMap(ub -> ub.getBranch().getId(), ub -> ub));

      java.util.Set<com.finprov.loan.entity.UserBranch> newSet = new java.util.HashSet<>();

      for (Long bid : branchIds) {
        if (existingMap.containsKey(bid)) {
          newSet.add(existingMap.get(bid));
        } else {
          com.finprov.loan.entity.Branch branch = branchRepository.findById(bid)
              .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + bid));
          com.finprov.loan.entity.UserBranch ub = com.finprov.loan.entity.UserBranch.builder()
              .user(user)
              .branch(branch)
              .assignedAt(java.time.Instant.now())
              .build();
          newSet.add(ub);
        }
      }
      user.getUserBranches().clear();
      user.getUserBranches().addAll(newSet);
    }

    return userRepository.save(user);
  }
}
