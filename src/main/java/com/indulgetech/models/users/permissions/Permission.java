package com.indulgetech.models.users.permissions;


import com.indulgetech.models.common.audit.AuditListener;
import com.indulgetech.models.common.audit.AuditSession;
import com.indulgetech.models.common.audit.Auditable;
import com.indulgetech.models.common.generics.BaseEntity;
import com.indulgetech.models.users.roles.Role;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static constants.SchemaConstant.TABLE_PERMISSIONS;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditListener.class)
@SQLDelete(sql =
        "UPDATE permissions " +
                "SET deleted = '1' " +
                "WHERE id = ?")
@Where(clause = "deleted='0'")
@Table(name=TABLE_PERMISSIONS)
public class Permission extends BaseEntity<Integer, Permission> implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true)
    private String permission;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
    }
    public void removeRole(Role role) {
        roles.remove(role);
    }

    @Embedded
    private AuditSession auditSession = new AuditSession();

    public Permission(String permission) {
        this.permission=permission;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", permission='" + permission + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", permissionType=" + permissionType +
                '}';
    }
}
