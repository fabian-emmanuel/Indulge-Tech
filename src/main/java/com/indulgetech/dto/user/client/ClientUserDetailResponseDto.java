package com.indulgetech.dto.user.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ClientUserDetailResponseDto {
    @JsonProperty(value = "user")
    private ClientUserDetailDto clientUserDetailDto;
    private Map<String,Object> extras;
}
