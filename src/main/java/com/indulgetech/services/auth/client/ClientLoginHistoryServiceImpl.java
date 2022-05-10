package com.indulgetech.services.auth.client;


import com.indulgetech.models.users.LoginHistory;
import com.indulgetech.models.users.client.ClientLoginHistory;
import com.indulgetech.repositories.user.ClientLoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ClientLoginHistoryServiceImpl implements ClientLoginHistoryService {

    private final ClientLoginHistoryRepository repository;

    @Override
    public LoginHistory saveLoginHistory(ClientLoginHistory loginHistory) {
        return repository.save(loginHistory);
    }

}
