package com.indulgetech.services.system;


import com.indulgetech.dto.system.SystemConfigurationDto;
import com.indulgetech.models.system.SystemConfiguration;
import com.indulgetech.services.generic.BaseEntityService;

import java.util.Collection;
import java.util.Map;

public interface SystemConfigurationService  extends BaseEntityService<Long, SystemConfiguration> {

    String findConfigValueByKey(String key);

    void createDefaultConfigurations();

    Collection<SystemConfigurationDto> fetchSystemConfigurations(String group);

    void updateSystemConfigurations(String group, Map<String,String> dto);
}
