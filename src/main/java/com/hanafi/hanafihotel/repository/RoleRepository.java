package com.hanafi.hanafihotel.repository;

import com.hanafi.hanafihotel.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String roleName);

    boolean existsByName(String roleName);
}
