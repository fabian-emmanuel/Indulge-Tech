package com.indulgetech.models.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemListingDto {
    protected String id;
    protected String name;
    public ItemListingDto(String id, String name) {
        this.id = id;
        this.name=name;
    }
}
