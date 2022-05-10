package com.indulgetech.models.users.roles;

import com.indulgetech.models.common.audit.AuditListener;
import com.indulgetech.models.common.audit.AuditSession;
import com.indulgetech.models.common.audit.Auditable;
import com.indulgetech.models.common.generics.BaseEntity;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.models.users.permissions.Permission;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static constants.SchemaConstant.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Builder
@Entity
@EntityListeners(AuditListener.class)
@SQLDelete(sql =
        "UPDATE roles " +
                "SET deleted = '1' " +
                "WHERE id = ?")
@Where(clause = "deleted='0'")
@Table(name= TABLE_ROLES)
public class Role extends BaseEntity<Integer, Role> implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length=100)
    private String name;
    private String description;

    @Column(length = 30)//TODO: should be unique, but setting it here makes tests fail for now when calling AdminUserDetail.getLoggedinToken()
    private String roleKey;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<AdminUser> adminUsers = new HashSet<>();
//
//    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
//    private Set<ClientUser> clientUsers = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    /*@JoinTable(name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))*/
    private Set<Permission> permissions = new HashSet<>();


    @Embedded
    private AuditSession auditSession = new AuditSession();

    private boolean systemCreated;

    public void addPermission(Permission permission) {
        permission.addRole(this);
        permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        permission.removeRole(this);
        permissions.remove(permission);
    }

//    public void addClientUser(ClientUser clientUser){
//        this.clientUsers.add(clientUser);
//    }
//
//    public void removeClientUser(ClientUser clientUser){
//        this.clientUsers.remove(clientUser);
//    }

   
    public void addAdminUser(AdminUser adminUser){
        this.adminUsers.add(adminUser);
    }

    public void removeAdminUser(AdminUser adminUser){
        this.adminUsers.remove(adminUser);
    }
    
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", roleKey='" + roleKey + '\'' +
                ", roleType=" + roleType +
                ", auditSection=" + auditSession +
                '}';
    }
}
