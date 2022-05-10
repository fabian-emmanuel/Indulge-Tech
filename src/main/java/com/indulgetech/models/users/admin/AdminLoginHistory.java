package com.indulgetech.models.users.admin;

import com.indulgetech.models.common.audit.AuditListener;
import com.indulgetech.models.users.LoginHistory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static constants.SchemaConstant.TABLE_ADMIN_LOGIN_HISTORY;


@Getter
@Setter
@EntityListeners(value = AuditListener.class)
@Table(name = TABLE_ADMIN_LOGIN_HISTORY)
@Entity
public class AdminLoginHistory extends LoginHistory {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private AdminUser user;
}
