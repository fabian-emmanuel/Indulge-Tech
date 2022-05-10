package com.indulgetech.models.users;

import com.indulgetech.models.common.audit.AuditSession;
import com.indulgetech.models.common.audit.Auditable;
import com.indulgetech.models.common.generics.BaseEntity;
import com.indulgetech.utils.CommonUtils;
import com.indulgetech.utils.CustomDateUtils;
import constants.SchemaConstant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.Where;
import org.springframework.boot.actuate.audit.listener.AuditListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;

import static constants.SchemaConstant.*;

@MappedSuperclass
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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

    public boolean tokenExpired() {
        return this.passwordResetValidityTerm != null && this.passwordResetValidityTerm.before(CustomDateUtils.now());
    }

    public void calculateTokenExpiryDate(String valdtyTrm){
        if (!"0" .equals(valdtyTrm)) { //0 means no validity term used
            if (!CommonUtils.isInteger(valdtyTrm)) {
                this.setDefaultPasswordValidityTerm();
            } else {
                if (Integer.parseInt(valdtyTrm) < 0) {
                    this.setDefaultPasswordValidityTerm();
                } else {
                    this.setPasswordResetValidityTerm(DateUtils.addHours(CustomDateUtils.now(), Integer.parseInt(valdtyTrm)));
                }
            }
        }
    }

    private void setDefaultPasswordValidityTerm() {
        this.setPasswordResetValidityTerm(DateUtils.addHours(CustomDateUtils.now(), DEFAULT_PWRD_SETTING_VLDTY_TRM));
    }
}
