package com.indulgetech.controllers.user.admin.login_history;


import com.indulgetech.services.auth.admin.AdminLoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${api.basepath-admin}/user/login-history")
public class AdminLoginHistoryController implements IAdminLoginHistoryController {

    private final AdminLoginHistoryService adminLoginHistoryService;

}
