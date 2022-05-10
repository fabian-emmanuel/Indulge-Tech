package com.indulgetech.services.auth.client;


import com.indulgetech.models.users.LoginHistory;
import com.indulgetech.models.users.client.ClientLoginHistory;

import java.util.Collection;

public interface ClientLoginHistoryService {
    LoginHistory saveLoginHistory(ClientLoginHistory clientLoginHistory);

}
