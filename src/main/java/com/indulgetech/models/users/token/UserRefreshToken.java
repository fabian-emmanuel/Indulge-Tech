package com.indulgetech.models.users.token;


import com.indulgetech.models.common.audit.AuditListener;
import com.indulgetech.models.common.audit.AuditSession;
import com.indulgetech.models.common.audit.Auditable;
import com.indulgetech.models.common.generics.BaseEntity;
import com.indulgetech.utils.CommonUtils;
import com.indulgetech.utils.CustomDateUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@EntityListeners(AuditListener.class)
public class UserRefreshToken extends BaseEntity<Long, UserRefreshToken> implements Auditable {

    public static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String token;

    private Date validityTrm;

    @Column(nullable = false)
    private String userName;

    @Embedded
    private AuditSession auditSession = new AuditSession();

    public void calculateExpiryDate(String valdtyTrm) {

        if (!"0".equals(valdtyTrm)) { //0 means no validity term used
            if (!CommonUtils.isInteger(valdtyTrm)) {
                this.setDefaultValidityTerm();
            } else {
                if (Integer.parseInt(valdtyTrm) < 0) {
                    this.setDefaultValidityTerm();
                } else {
                    setValidityTrm(DateUtils.addMinutes(CustomDateUtils.now(), Integer.parseInt(valdtyTrm)));
                }
            }
        }

    }

    private void setDefaultValidityTerm() {
        setValidityTrm(DateUtils.addMinutes(CustomDateUtils.now(), EXPIRATION));
    }

    public boolean isExpired() {
        return this.validityTrm.before(CustomDateUtils.now());
    }
}
