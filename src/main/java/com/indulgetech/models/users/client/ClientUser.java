package com.indulgetech.models.users.client;

import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.User;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static constants.SchemaConstant.TABLE_CLIENT_USERS;
import static constants.SchemaConstant.TABLE_CLIENT_USER_ROLE;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Where(clause = "deleted='0'")
@Table(name=TABLE_CLIENT_USERS)
public class ClientUser extends User {
//    @ManyToOne
//    Role role;


    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = TABLE_CLIENT_USER_ROLE,
            joinColumns = @JoinColumn(name = "client_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
        role.addClientUser(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.removeClientUser(this);
    }

}
