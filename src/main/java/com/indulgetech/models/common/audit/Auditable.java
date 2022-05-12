package com.indulgetech.models.common.audit;

public interface Auditable {
    AuditSection getAuditSection();
    void setAuditSection(AuditSection auditSection);
}
