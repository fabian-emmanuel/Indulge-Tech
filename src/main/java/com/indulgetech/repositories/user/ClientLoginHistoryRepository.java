package com.indulgetech.repositories.user;

import com.indulgetech.models.users.client.ClientLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientLoginHistoryRepository extends JpaRepository<ClientLoginHistory, Long> {
}
