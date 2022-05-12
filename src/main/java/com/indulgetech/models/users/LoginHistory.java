package com.indulgetech.models.users;


import com.indulgetech.constants.PrcRsltCode;
import com.indulgetech.models.common.audit.AuditListener;
import com.indulgetech.models.common.audit.AuditSection;
import com.indulgetech.models.common.audit.Auditable;
import com.indulgetech.models.common.generics.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@MappedSuperclass
@EntityListeners(value = AuditListener.class)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public class LoginHistory extends BaseEntity<Long, LoginHistory> implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "IP_ADDR")
    private String ipAddr;

    @Column(name = "PRC_RSLT", columnDefinition = "varchar(1) not null")
    private String prcRslt= PrcRsltCode.FAILURE; //failure by default

    @Embedded
    private AuditSection auditSection = new AuditSection();

}
