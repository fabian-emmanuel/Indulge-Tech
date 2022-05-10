package com.indulgetech.services.auth.client;

import com.indulgetech.dto.auth.CreatePasswordDto;

public interface PasswordService {
    boolean createOrganisationUserPassword(CreatePasswordDto createPasswordDto);

    boolean verifyToken(String token);
}
