package com.indulgetech.dto.system;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude()
public class SystemConfigurationDto {
    private String name;
    private String value;
    private String key;
    private String description;
    private String type;
    private ConfigurationMetadata metadata;
}

