package com.indulgetech.models.common.audit;

public interface Auditable {
    AuditSession getAuditSession();
    void setAuditSession(AuditSession auditSession);
}
