package com.indulgetech.integration.user.admin;

import com.indulgetech.AbstractTest;
import com.indulgetech.constants.Constants;
import com.indulgetech.dto.user.admin.AdminUserRequestDto;
import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.roles.RoleType;
import com.indulgetech.repositories.permission.PermissionRepository;
import com.indulgetech.repositories.role.RoleRepository;
import com.indulgetech.repositories.user.AdminUserRepository;
import com.indulgetech.utils.AdminUserDetails;
import com.indulgetech.utils.TestModels;
import com.indulgetech.utils.TestUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
////@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
//@ExtendWith(DBCleanerExtension.class)
class AdminUserTest extends AbstractTest {

    @Value("${api.basepath-admin}")
    private String path;

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;


    @Autowired
    private AdminUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    AdminUserDetails userDetails;

    @Autowired
    EntityManager entityManager;

//    @Autowired
//    DBCleanerUtil dbCleanerUtil;

//    @Autowired
//    TxnManager txnManager;

    @BeforeAll
    void setUpBeforeClass() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @BeforeEach
    public void setUpBeforeEach(TestInfo testInfo) throws Exception {
        if (testInfo.getTags().contains("parameterized")) return;
        Role loggedInUserRole = new Role();
        loggedInUserRole.setRoleKey(Constants.ROLE_SUPER_ADMIN);
        loggedInUserRole.setName("Administrator");
        loggedInUserRole.setRoleType(RoleType.ADMIN);
        //roleRepository.save(loggedInUserRole);
        String token = userDetails.getLoggedInToken("admin@gmail.com", "08103169369", loggedInUserRole);
        headers.add("Authorization", "Bearer " + token);
    }

    @AfterEach
    void tearDown() throws Exception {

    }

    @AfterAll
    public void tearDownAll() {
    }

    private void setUpAdmin() {
        String token = userDetails.getLoggedInToken("admin@gmail.com", "08103169369");
        headers.add("Authorization", "Bearer " + token);
    }


    @Test
    void shouldCreateAdminUserSuccessfully() throws Exception {
        //arrange
//        txnManager.startTxn();
        Map<String, Permission> perms = new HashMap<>();
        Arrays.asList("add_user", "edit_user", "list_users", "list_organisations", "edit_organisation", "add_organisation").forEach(perm -> {
            perms.put(perm, this.permissionRepository.save(new Permission(perm)));
        });
        //roles
        //role1
        Role role1 = TestModels.role("Member", RoleType.ADMIN);
        role1.setRoleKey(Constants.ROLE_ADMIN_MEMBER);
        Arrays.asList(perms.get("add_user"), perms.get("edit_user"),
                perms.get("list_users")).forEach(role1::addPermission);
        //role2
        Role role2 = TestModels.role("User Manager", RoleType.ADMIN);
        Arrays.asList(perms.get("add_user"), perms.get("edit_user"),
                perms.get("list_organisations"), perms.get("edit_organisation")).forEach(role2::addPermission);

        this.roleRepository.saveAll(List.of(role1, role2));
//        txnManager.endTxn();
        //act
        //input
        AdminUserRequestDto dto = new AdminUserRequestDto();
        dto.setFirstName("olalekan");
        dto.setLastName("oloba");
        dto.setEmail("mydevemail2020@gmail.com");
        dto.setPhone("08137640746");
        //dto.setPassword("olalekan.oloba");
        //roles
        dto.setRoles(List.of(role1.getId(), role2.getId()));

        //act/assert
        MvcResult result = this.mockMvc
                .perform(post(path + "/users").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", Is.is("Resource created successfully")))
                .andExpect(jsonPath("$.data.id").value(Matchers.greaterThan(0))).andReturn();
        Integer id = TestUtils.objectFromResponseStr(result.getResponse().getContentAsString(), "$.data.id");
        //assert user created
//        txnManager.startTxn();
        AdminUser user = this.userRepository.findById(id.longValue()).get();
        assertNotNull(user);
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().containsAll(List.of(role1, role2)));
//        txnManager.endTxn();
        //assert user can login
     /*   LoginRequest loginRequest = new LoginRequest("mydevemail2020@gmail.com", "olalekan.oloba");
        //act
        result=this.mockMvc
                .perform(MockMvcRequestBuilders.post(path+"/login").content(TestUtils.asJsonString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user.userId").value(user.getId())).andReturn();

        String authorities = repositoryUtils.getAuthorities(TestUtils.objectFromResponseStr(result.getResponse().getContentAsString(), "$.data.token"));
        Collection<String> authPerms = Arrays.asList(authorities.split(AUTHORITIES_CLAIM_KEY_SEP));
        //assertEquals(4,authPerms.size());//todo this shud pass duplicate perms needs to be filtered
        assertTrue(authPerms.containsAll(List.of("add_user", "edit_user", "list_users", "edit_organisation","list_organisations")));*/

    }


//    @Test
//    void shouldUpdateAdminUserSuccessfully() throws Exception {
//        //arrange
//        //all roles
//        Role role1 = TestModels.role("Member", RoleType.ADMIN);
//        role1.setRoleKey(Constants.ROLE_ADMIN_MEMBER);
//        Role role2 = TestModels.role("User Manager", RoleType.ADMIN);
//        Role role3 = TestModels.role("Subscription Manager", RoleType.ADMIN);
//        this.roleRepository.saveAll(List.of(role1, role2, role3));
//        //user
//        AdminUser user = TestModels.adminUser("olalekan", "ale", "", "admin1@gmail.com",
//                "", "08137640746");
//        user.setStatus(UserStatus.ACTIVE);
//        //defined user roles
//        //user.addRole(role2);
//        this.userRepository.save(user);
//        //act
//        // input
//        AdminUserRequestDto dto = new AdminUserRequestDto();
//        dto.setFirstName("oluwaseni");
//        dto.setLastName("oloba");
//        dto.setEmail("admin2@gmail.com");
//        dto.setPhone("08037640746");
//        dto.setStatus(UserStatus.INACTIVE.name());
//        //roles
//        dto.setRoles(List.of(role1.getId(), role3.getId()));
//
//        mockMvc.perform(put(path + "/users/{userId}", user.getId()).headers(headers).contentType("application/json")
//                        .content(TestUtils.asJsonString(dto))).andExpect(status().isOk())
//                .andExpect(jsonPath("$.message", Is.is("Resource updated successfully")));
//        //assert
//        txnManager.startTxn();
//        AdminUser updatedUser = this.userRepository.findById(user.getId()).get();
//        assertEquals("oluwaseni", updatedUser.getFirstName());
//        assertEquals("oloba", updatedUser.getLastName());
//        assertEquals("admin2@gmail.com", updatedUser.getEmail());
//        assertEquals("oloba", updatedUser.getLastName());
//        assertEquals("08037640746", updatedUser.getTelephoneNumber());
//        assertEquals(UserStatus.INACTIVE.name(), updatedUser.getStatus().name());
//        assertEquals(FellowshipDateUtils.today(), FellowshipDateUtils.getDateFromDateTime(updatedUser.getStatusDate()));
//        AdminUser loggedInUser = this.userRepository.findByEmail("admin@gmail.com").get();
//        assertEquals(loggedInUser.getId(), updatedUser.getAuditSection().getModifiedBy());
//        //roles updated
//        assertEquals(2, updatedUser.getRoles().size());
//        assertTrue(updatedUser.getRoles().stream().map(Role::getId).collect(Collectors.toList()).containsAll(dto.getRoles()));
//        txnManager.endTxn();
//    }
//
//
//    @Test
//    void shouldDeactivateAdminUserAccountSuccessfully() throws Exception {
//        //arrange
//        //user
//        AdminUser user = TestModels.adminUser("olalekan", "ale", "", "admin1@gmail.com",
//                "", "08137640746");
//        user.setStatus(UserStatus.ACTIVE);
//        this.userRepository.save(user);
//        //act
//        mockMvc.perform(put(path + "/users/{userId}/deactivate", user.getId()).headers(headers)).andExpect(status().isOk())
//                .andExpect(jsonPath("$.message", Is.is("Resource deactivated successfully")));
//
//        AdminUser deactivatedUser = this.userRepository.findById(user.getId()).get();
//        assertEquals(UserStatus.INACTIVE.name(), deactivatedUser.getStatus().name());
//        //assertEquals(FellowshipDateUtils.today(), deactivatedUser.getStatusDate());
//        AdminUser loggedInUser = this.userRepository.findByEmail("admin@gmail.com").get();
//        assertEquals(loggedInUser.getId(), deactivatedUser.getAuditSection().getModifiedBy());
//    }
//
//
    @Test
    void shouldActivateAdminUserAccountSuccessfully() throws Exception {
        //arrange
        //user
        AdminUser user = TestModels.adminUser("olalekan", "ale", "", "admin1@gmail.com",
                "", "08137640746");
        user.setStatus(UserStatus.INACTIVE);
        this.userRepository.save(user);
        //act
        mockMvc.perform(put(path + "/users/{userId}/activate", user.getId()).headers(headers)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resource deactivated successfully")));

        AdminUser activatedUser = this.userRepository.findById(user.getId()).get();
        assertEquals(UserStatus.ACTIVE.name(), activatedUser.getStatus().name());
        //assertEquals(FellowshipDateUtils.today(), deactivatedUser.getStatusDate());
        AdminUser loggedInUser = this.userRepository.findByEmail("admin@gmail.com").get();
        assertEquals(loggedInUser.getId(), activatedUser.getAuditSession().getModifiedBy());
    }



}
