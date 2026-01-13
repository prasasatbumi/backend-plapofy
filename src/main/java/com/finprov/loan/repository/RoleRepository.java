package com.finprov.loan.repository;

import com.finprov.loan.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);

  List<Role> findAllByDeletedFalseOrDeletedIsNull();

  Optional<Role> findByIdAndDeletedFalseOrDeletedIsNull(Long id);

  @Query(value = "SELECT * FROM roles WHERE role_name = :name", nativeQuery = true)
  Optional<Role> findByNameIncludingDeleted(@Param("name") String name);

  @Query(value = "SELECT * FROM roles WHERE id = :id", nativeQuery = true)
  Optional<Role> findByIdIncludingDeleted(@Param("id") Long id);
}
