package com.indulgetech.models.users;


import com.indulgetech.models.common.ItemListingDto;

import java.util.ArrayList;
import java.util.Collection;

public enum UserStatus {

    ACTIVE, INACTIVE;

    public static Collection<ItemListingDto> toItemList() {
        Collection<ItemListingDto> list = new ArrayList<>();
        for (UserStatus userStatus : UserStatus.values()) {
            list.add(new ItemListingDto(userStatus.name(), userStatus.name()));
        }
        return list;
    }
}
