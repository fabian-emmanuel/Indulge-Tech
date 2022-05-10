package com.indulgetech.services.auth.admin;


import com.indulgetech.models.users.LoginHistory;
import com.indulgetech.models.users.admin.AdminLoginHistory;
import com.indulgetech.repositories.user.AdminLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class AdminLoginHistoryServiceImpl implements AdminLoginHistoryService {

    private final AdminLoginHistoryRepository repository;

    @Override
    public LoginHistory saveLoginHistory(AdminLoginHistory loginHistory) {
        return repository.save(loginHistory);
    }

}
