package com.indulgetech.integration.user.permission;


import com.indulgetech.AbstractTest;
import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.permissions.PermissionType;
import com.indulgetech.repositories.permission.PermissionRepository;
import com.indulgetech.utils.AdminUserDetails;
import com.indulgetech.utils.DBCleanerUtil;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PermissionTest extends AbstractTest {

    @Value("${api.basepath-admin}")
    private String path;

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    DBCleanerUtil dbCleanerUtil;

    @Autowired
    AdminUserDetails userDetails;

    @BeforeAll
    void setUpBeforeClass() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @BeforeEach
    public void setUpBeforeEach() throws Exception {
        dbCleanerUtil.cleanDb();
        String token = userDetails.getLoggedInToken("admin@gmail.com", "08103169369");
        headers.add("Authorization", "Bearer " + token);
    }

    @AfterEach
    void tearDown() throws Exception {

    }


    @Test
    void shouldListPermissionsSuccessfully() throws Exception {
        //arrange
        //permissions
        Permission perm = new Permission("SUPERADMIN");
        perm.setName("SUPERADMIN PERM TITLE");
        perm.setDescription("Has access to all System functions");
        perm.setPermissionType(PermissionType.ADMIN);
        this.permissionRepository.save(perm);

        perm = new Permission("ADMIN");
        perm.setName("ADMIN");
        perm.setDescription("Has access to all System functions");
        perm.setPermissionType(PermissionType.ADMIN);
        this.permissionRepository.save(perm);

        //act/assert
        mockMvc.perform(get(path + "/permissions").headers(headers)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resources retrieved successfully")))
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("SUPERADMIN"))
                .andExpect(jsonPath("$.data[0].name").value("SUPERADMIN PERM TITLE"))
                .andExpect(jsonPath("$.data[0].description").value("Has access to all System functions"))
                .andExpect(jsonPath("$.data[1].id").value("ADMIN"));
    }


}