package com.indulgetech.dto.system;

import com.indulgetech.models.common.ItemListingDto;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;

@Data
public class ConfigurationMetadata {
    Collection<ItemListingDto> list= Collections.emptyList();
}
