package com.indulgetech.models.users.token;

import com.indulgetech.models.common.audit.AuditListener;
import com.indulgetech.models.users.client.ClientUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static constants.SchemaConstant.TABLE_CUSTOMER_REFRESH_TOKEN;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditListener.class)
@Table(name = TABLE_CUSTOMER_REFRESH_TOKEN)
public class CustomerRefreshToken extends UserRefreshToken {

    @ManyToOne(fetch = FetchType.LAZY)
    private ClientUser organizationUser;

}
