package com.indulgetech.repositories.system;

import com.indulgetech.models.system.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration,Long> {
    @Query("select s.value from SystemConfiguration s where s.configurationKey = ?1")
    String findByConfigurationKey(String key);

    Collection<SystemConfiguration> findByConfigurationGroupOrderBySortOrderAsc(String group);
}
