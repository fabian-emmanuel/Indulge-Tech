package com.indulgetech.repositories.user;

import com.indulgetech.models.users.admin.AdminLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLoginHistoryRepository extends JpaRepository<AdminLoginHistory, Long>{
}
