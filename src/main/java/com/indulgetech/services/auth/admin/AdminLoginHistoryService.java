package com.indulgetech.services.auth.admin;


import com.indulgetech.models.users.LoginHistory;
import com.indulgetech.models.users.admin.AdminLoginHistory;

import java.util.Collection;

public interface AdminLoginHistoryService {
    LoginHistory saveLoginHistory(AdminLoginHistory loginHistory);

}
