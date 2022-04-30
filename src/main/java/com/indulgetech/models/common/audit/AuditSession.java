package com.indulgetech.models.common.audit;

import com.indulgetech.utils.CloneUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Embeddable
//@Getter
//@Setter
//@NoArgsConstructor
public class AuditSession implements Serializable {
    @Serial
    private static final long serialVersionUID = -1934446958975060889L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_modified")
    private Date dateModified;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name="deleted", columnDefinition = "varchar(1) default (0)")
    private String delF="0";

//    public AuditSession() {}

    public Date getDateCreated() {
        return CloneUtils.clone(dateCreated);
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = CloneUtils.clone(dateCreated);
    }

    public Date getDateModified() {
        return CloneUtils.clone(dateModified);
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = CloneUtils.clone(dateModified);
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getDelF() {
        return delF;
    }

    public void setDelF(String delF) {
        this.delF = delF;
    }

    @Override
    public String toString() {
        return "AuditSection{" +
                "dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", delF='" + delF + '\'' +
                '}';
    }
}
