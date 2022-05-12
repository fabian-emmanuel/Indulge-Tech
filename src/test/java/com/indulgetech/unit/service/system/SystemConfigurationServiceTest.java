package com.indulgetech.unit.service.system;


import com.indulgetech.AbstractTest;
import com.indulgetech.models.system.SystemConfiguration;
import com.indulgetech.repositories.system.SystemConfigurationRepository;
import com.indulgetech.services.system.SystemConfigurationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemConfigurationServiceTest {

    /**
     * System under test (SUT)
     */
    @InjectMocks
    SystemConfigurationServiceImpl systemConfigurationService;

    @Mock
    SystemConfigurationRepository repository;
    

    @Test
    void shouldFindByKeySuccessfully() throws Exception {
        //arrange
        SystemConfiguration systemConfiguration = new SystemConfiguration();
        systemConfiguration.setValue("systemConfiguration");
        systemConfiguration.setConfigurationKey("key");
        //actions
        when(repository.findByConfigurationKey(anyString())).thenReturn(systemConfiguration.getValue());
        assertNotNull(systemConfigurationService.findConfigValueByKey(systemConfiguration.getConfigurationKey()));
    }

    @Test
    void shouldReturnNullWhenFindByKeyAndKeyNotExist() throws Exception {
        //act
        String notExistKey="notexistkey";
        when(repository.findByConfigurationKey(anyString())).thenReturn("");
        assertEquals("", systemConfigurationService.findConfigValueByKey(notExistKey));
    }

}
