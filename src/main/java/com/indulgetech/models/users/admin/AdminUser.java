package com.indulgetech.models.users.admin;

import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.User;
import lombok.*;
import org.springframework.boot.actuate.audit.listener.AuditListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static constants.SchemaConstant.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditListener.class)
@Table(name = TABLE_ADMIN_USER)
public class AdminUser extends User {
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = TABLE_ADMIN_USER_ROLE,
            joinColumns = @JoinColumn(name = "admin_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
        role.addAdminUser(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.removeAdminUser(this);
    }
}
