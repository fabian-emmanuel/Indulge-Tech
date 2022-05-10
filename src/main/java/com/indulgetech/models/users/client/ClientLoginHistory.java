package com.indulgetech.models.users.client;


import com.indulgetech.models.common.audit.AuditListener;
import com.indulgetech.models.users.LoginHistory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static constants.SchemaConstant.TABLE_CLIENT_LOGIN_HISTORY;

@Getter
@Setter
@EntityListeners(value = AuditListener.class)
@Table(name = TABLE_CLIENT_LOGIN_HISTORY)
@Entity
public class ClientLoginHistory extends LoginHistory {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private ClientUser user;
}
