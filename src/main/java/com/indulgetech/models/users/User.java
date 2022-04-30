package com.indulgetech.models.users;

import com.indulgetech.models.common.audit.AuditSession;
import com.indulgetech.models.common.audit.Auditable;
import com.indulgetech.models.common.generics.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.boot.actuate.audit.listener.AuditListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(AuditListener.class)
@Getter
@Setter
//@Entity
@Where(clause = "deleted='0'")
public abstract class User extends BaseEntity<Long, User> implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Email(message = "please enter a valid email value")
    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 64)
    private String password;

    @Column(length = 100)
    private String passwordResetToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordResetValidityTerm;

    @Temporal(TemporalType.DATE)
    private Date lastLogin;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Temporal(TemporalType.DATE)
    private Date statusDate;

    @Column(length = 100)
    private String telephoneNumber;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(length=64)
    private String profilePic;

    @Embedded
    private AuditSession auditSession = new AuditSession();
}
