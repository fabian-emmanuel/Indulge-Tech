package com.indulgetech.models.common.audit;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import static com.indulgetech.utils.CustomDateUtils.*;

public class AuditListener {

  @PrePersist
  public void onSave(Object o) {
    if (o instanceof Auditable audit) {
      AuditSession auditSection = audit.getAuditSession();

      auditSection.setDateModified(now());
      if (auditSection.getDateCreated() == null) {
        auditSection.setDateCreated(now());
      }
      audit.setAuditSession(auditSection);
    }
  }

  @PreUpdate
  public void onUpdate(Object o) {
    if (o instanceof Auditable audit) {
      AuditSession auditSection = audit.getAuditSession();

      auditSection.setDateModified(now());
      if (auditSection.getDateCreated() == null) {
        auditSection.setDateCreated(now());
      }
      audit.setAuditSession(auditSection);
    }
  }
}
