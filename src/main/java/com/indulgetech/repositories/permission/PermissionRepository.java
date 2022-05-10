package com.indulgetech.repositories.permission;


import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.permissions.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission,Integer> {
    Optional<Permission> findByPermission(String permKey);

    Collection<Permission> findByPermissionIn(Collection<String> names);

    Collection<Permission> findAllByPermissionType(PermissionType permissionType);
}
