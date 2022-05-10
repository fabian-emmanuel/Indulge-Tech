package com.indulgetech.models.system;


import com.indulgetech.models.common.audit.AuditListener;
import com.indulgetech.models.common.audit.AuditSession;
import com.indulgetech.models.common.audit.Auditable;
import com.indulgetech.models.common.generics.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static constants.SchemaConstant.TABLE_SYSTEM_CONFIGURATION;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditListener.class)
@Table(name = TABLE_SYSTEM_CONFIGURATION)
public class SystemConfiguration extends BaseEntity<Long, SystemConfiguration> implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=100)
    private String configurationName;

    private String value;

    @Column(length=50,unique = true)
    private String configurationKey;

    private String description;

    @Column(length=30)
    private String configurationGroup;

    private int sortOrder;

    @Enumerated(EnumType.STRING)
    private SystemConfigrationType systemConfigrationType=SystemConfigrationType.TEXT;

    @Embedded
    private AuditSession auditSession = new AuditSession();
}
