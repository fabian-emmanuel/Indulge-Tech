package com.indulgetech.dto.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListingDto {
    protected Long id;
    protected String name;
    public ListingDto(Long id, String name) {
        this.id = id;
        this.name=name;
    }
}
