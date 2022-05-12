package com.indulgetech.integration.user.role;


import com.indulgetech.AbstractTest;
import com.indulgetech.dto.user.role.RoleRequestDto;
import com.indulgetech.models.users.UserType;
import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.permissions.PermissionType;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.roles.RoleType;
import com.indulgetech.repositories.permission.PermissionRepository;
import com.indulgetech.repositories.role.RoleRepository;
import com.indulgetech.repositories.user.ClientUserRepository;
import com.indulgetech.utils.*;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class RoleTest extends AbstractTest {

    @Value("${api.basepath}")
    private String path = "";

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    AdminUserDetails userDetails;

    @Autowired
    ClientUserDetails clientUserDetails;

    @Autowired
    private ClientUserRepository clientUserRepository;


    @Autowired
    EntityManager em;

    @Autowired
    DBCleanerUtil dbCleanerUtil;


    @BeforeAll
    void setUpBeforeClass() throws Exception {

    }

    @BeforeEach
    public void setUpBeforeEach() throws Exception {
        dbCleanerUtil.cleanDb();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    private void setToken(UserType userType) {
        String token;
        if (userType.equals(UserType.ADMIN)) {
            token = userDetails.getLoggedInToken("admin@gmail.com", "08103169369");
        } else {
            //arrange

            token = clientUserDetails.getLoggedInToken("client@gmail.com", "08103169369");
        }
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    @Test
    @Transactional
//used only in this method to make test run
    void shouldCreateRoleSuccessfully() throws Exception {
        this.setToken(UserType.ADMIN);
        // arrange
        Arrays.asList("add_user", "edit_user").forEach(permission -> {
            this.permissionRepository.save(new Permission(permission));
        });
        // input
        RoleRequestDto dto = new RoleRequestDto();
        Collection<String> rolePerms = Arrays
                .asList("add_user", "edit_user");
        dto.setName("User Manager");
        dto.setPermissions(rolePerms);
        dto.setRoleType(RoleType.ADMIN.name());
        dto.setDescription("Manages users");
        //act
        this.mockMvc
                .perform(post(path + "/roles").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(Matchers.greaterThan(0)));

        List<Role> roles = this.roleRepository.findAll();
        assertNotNull(roles);
        assertEquals(2, roles.size()); //one initially created to generate logged in token
        Role role = roles.stream().filter((r -> r.getName().equals("User Manager"))).iterator().next();
        assertNotNull(role);
        assertNotNull(role.getAuditSection().getDateCreated());
        assertEquals(RoleType.ADMIN, role.getRoleType());
        //assert organisation is null since role is created by admin portal
        //perms created
        assertEquals(2, role.getPermissions().size());
        assertTrue(role.getPermissions().stream().map(Permission::getPermission).toList().containsAll(rolePerms));
    }

    @Test
    @Transactional
//used only in this method to make test run
    void shouldCreateRoleWithClientRoleTypeSuccessfully() throws Exception {
        this.setToken(UserType.ADMIN);
        // input
        RoleRequestDto dto = new RoleRequestDto();
        dto.setName("User Manager");
        dto.setRoleType(RoleType.CLIENT.name());
        dto.setDescription("Manages users");
        //act
        MvcResult result = this.mockMvc
                .perform(post(path + "/roles").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(Matchers.greaterThan(0))).andReturn();

        Role newRole = this.roleRepository.getById(TestUtils.objectFromResponseStr(result.getResponse().getContentAsString(), "$.data.id"));
        assertNotNull(newRole);
        assertEquals(RoleType.CLIENT, newRole.getRoleType());
    }

    @Test
    @Transactional
//used only in this method to make test run
    void shouldCreateRoleWithNoPermissionsSuccessfully() throws Exception {
        this.setToken(UserType.ADMIN);
        // arrange
        // input
        RoleRequestDto dto = new RoleRequestDto();
        dto.setName("User Manager");
        dto.setPermissions(Collections.emptyList());
        dto.setDescription("Manages users");
        dto.setRoleType(RoleType.ADMIN.name());
        //act
        this.mockMvc
                .perform(post(path + "/roles").content(TestUtils.asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON).headers(headers).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(Matchers.greaterThan(0)));

        List<Role> roles = this.roleRepository.findAll();
        assertNotNull(roles);
        Role role = roles.stream().filter((r -> r.getName().equals("User Manager"))).iterator().next();
        assertNotNull(role);
        assertEquals(RoleType.ADMIN, role.getRoleType());
        assertNotNull(role.getAuditSection().getDateCreated());
        //assert no perms created
        assertEquals(0, role.getPermissions().size());
    }


    //LIST ROLES FOR ADMIN MANAGEMENT
    @Test
    void shouldListAllRolesSuccessfully() throws Exception {
        this.setToken(UserType.ADMIN);
        //arrange
        //roles
        Role role1 = TestModels.role("Engineer manager", RoleType.ADMIN);
        role1.setDescription("manage engr");
        role1.setSystemCreated(true);

        Role role2 = TestModels.role("Request Manager", RoleType.GLOBAL);
        role2.setDescription("manage requests");

        Role role3 = TestModels.role("Subscription Manager", RoleType.CLIENT);
        role3.setDescription("manage subscrs");

        this.roleRepository.saveAll(List.of(role1, role2, role3));
        //act/assert
        //ordererd by name
        mockMvc.perform(get(path + "/roles/manage").headers(headers)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resources retrieved successfully")))
                .andExpect(jsonPath("$.data.size()").value(4))//one admin role created to get token
                .andExpect(jsonPath("$.data[1].id").value(role1.getId()))
                .andExpect(jsonPath("$.data[1].name").value("Engineer manager"))
                .andExpect(jsonPath("$.data[1].roleType").value(RoleType.ADMIN.name()))
                .andExpect(jsonPath("$.data[1].description").value("manage engr"))
                .andExpect(jsonPath("$.data[1].isSystemCreated").value(true))
                .andExpect(jsonPath("$.data[2].id").value(role2.getId()))
                .andExpect(jsonPath("$.data[2].name").value("Request Manager"))
                .andExpect(jsonPath("$.data[2].isSystemCreated").value(false));
    }


    //LIST ROLES FOR DROPDOWNS AND MENUS
   /* @Test
    void shouldListOnlyAdminRoles_WhenLoggedInAsAdminUserSuccessfully() throws Exception {
        this.setToken(UserType.ADMIN);
        //arrange
        //roles
        Role role1 = TestModels.role("User Manager", RoleType.ADMIN);
        role1.setDescription("manage users");
        Role role2 = TestModels.role("Request Manager", RoleType.GLOBAL);
        role2.setDescription("manage requests");
        Role role3 = TestModels.role("Subscription Manager", RoleType.CLIENT);
        role3.setDescription("manage subscrs");

        this.roleRepository.saveAll(List.of(role1, role2, role3));

        //act/assert
        //ordererd by name
        mockMvc.perform(get(path + "/roles/list").headers(headers)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resources retrieved successfully")))
                .andExpect(jsonPath("$.data.size()").value(2));//one admin role created to get token
    }*/


    /*@Test
    void shouldListOnlyClientRoles_WhenLoggedInAsClient() throws Exception {
        this.setToken(UserType.CLIENT);
        //arrange
        //roles
        Role role1 = TestModels.role("User Manager", RoleType.CLIENT);
        role1.setDescription("manage users");
        Role role2 = TestModels.role("Request Manager", RoleType.GLOBAL);
        role2.setDescription("manage requests");

        this.roleRepository.saveAll(List.of(role1, role2));

        //act/assert
        //ordererd by name
        mockMvc.perform(get(path + "/roles/list").headers(headers)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resources retrieved successfully")))
                .andExpect(jsonPath("$.data.size()").value(3))
                .andExpect(jsonPath("$.data[1].id").value(role2.getId()))
                .andExpect(jsonPath("$.data[1].name").value("Request Manager"))
                .andExpect(jsonPath("$.data[1].roleType").value(RoleType.GLOBAL.name()))
                .andExpect(jsonPath("$.data[1].description").value("manage requests"))
                .andExpect(jsonPath("$.data[2].id").value(role1.getId()))
                .andExpect(jsonPath("$.data[2].name").value("User Manager"))
                .andExpect(jsonPath("$.data[2].description").value("manage users"))
                .andExpect(jsonPath("$.data[2].roleType").value(RoleType.CLIENT.name()));

    }*/


    @Test
    void shouldListRolesByTypeSuccessfully() throws Exception {
        //arrange
        this.setToken(UserType.ADMIN);
        //roles
        Role role1 = TestModels.role("User Manager", RoleType.CLIENT);
        role1.setDescription("manage users");
        Role role2 = TestModels.role("Request Manager", RoleType.GLOBAL);
        role2.setDescription("manage requests");
        Role role3 = TestModels.role("User Manager", RoleType.ADMIN);
        role3.setDescription("manage requests");
        Role role4 = TestModels.role("Engineer Manager", RoleType.ADMIN);
        role4.setDescription("manage engr");
        this.roleRepository.saveAll(List.of(role1, role2, role3, role4));
        //act/assert
        //order by name
        //filter roles by type
        //admin only role type
        mockMvc.perform(get(path + "/roles/list?type=admin").headers(headers)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resources retrieved successfully")))
                .andExpect(jsonPath("$.data.size()").value(3))//plus role created for token
                .andExpect(jsonPath("$.data[*].id", Matchers.hasItems(role3.getId(), role4.getId())))
                .andExpect(jsonPath("$.data[1].id").value(role4.getId()))
                .andExpect(jsonPath("$.data[1].name").value("Engineer Manager"))
                .andExpect(jsonPath("$.data[1].roleType").value(RoleType.ADMIN.name()))
                .andExpect(jsonPath("$.data[1].description").value("manage engr"))
        ;

        //admin only role type
        mockMvc.perform(get(path + "/roles/list?type=client").headers(headers)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resources retrieved successfully")))
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[*].id", Matchers.hasItems(role1.getId(), role2.getId())));

    }



  /*  @Test
    void shouldNotListAdminRoles_WhenLoggedInAsClient() throws Exception {
        this.setToken(UserType.CLIENT);
        //arrange
        //roles
        Role role1 = TestModels.role("User Manager", RoleType.ADMIN);
        role1.setDescription("manage users");
        Role role2 = TestModels.role("Request Manager", RoleType.ADMIN);
        role2.setDescription("manage requests");

        this.roleRepository.saveAll(List.of(role1, role2));
        //act/assert
        //ordererd by name
        mockMvc.perform(get(path + "/roles/list").headers(headers)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resources retrieved successfully")))
                .andExpect(jsonPath("$.data.size()").value(1));
    }*/


    @Test
    @Transactional
//note: test passes without this, but used here to make this method work internally
    void shouldFetchRoleDetailSuccessfully() throws Exception {
        //arrange
        this.setToken(UserType.ADMIN);
        Arrays.asList("add_user", "edit_user", "list_stacks", "edit_stack", "add_stack").forEach(permission -> {
            Permission perm = new Permission(permission);
            perm.setPermissionType(PermissionType.ADMIN);
            this.permissionRepository.save(perm);
        });
        Role role = TestModels.role("User Manager", RoleType.ADMIN);
        role.setDescription("manage users");

        Collection<String> roleAssignedPerms = Arrays.asList("add_user", "edit_user");
        roleAssignedPerms.forEach(permission -> {
            //transactional added to make this work
            role.addPermission(this.permissionRepository.findByPermission(permission).get());
        });
        this.roleRepository.save(role);
        //act
        //input
        long roleId = role.getId();
        //act/assert
        MvcResult result = mockMvc.perform(get(path + "/roles/{roleId}", roleId).headers(headers))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Resource retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(role.getId()))
                .andExpect(jsonPath("$.data.name").value("User Manager"))
                .andExpect(jsonPath("$.data.description").value("manage users"))
                .andExpect((jsonPath("$.data.permissions.size()").value("5"))).andReturn();
        List<?> l = TestUtils.objectFromResponseStr(result.getResponse().getContentAsString(),
                "$.data.permissions[?(@.checked==true)]");
        assertEquals(2, l.size());
    }


    @Test
    @Transactional
//note: test passes without this, but used here to make this method work internally
    void shouldFetchRoleDetailAllRoleType_WhenLoggedInAsAdmin() throws Exception {
        this.setToken(UserType.ADMIN);
        //arrange
        Role role = TestModels.role("User Manager", RoleType.CLIENT);
        role.setDescription("manage users");
        this.roleRepository.save(role);
        //act
        //input
        long roleId = role.getId();
        //act/assert
        mockMvc.perform(get(path + "/roles/{roleId}", roleId).headers(headers))
                .andExpect(status().isOk());
    }


    @Test
    void shouldReturn404NotFoundWhenFetchRoleAndRoleIsSoftDeleted() throws Exception {
        this.setToken(UserType.ADMIN);
        //arrange
        //role
        Role role = TestModels.role("User Manager", RoleType.ADMIN);
        role.setDescription("manage users");
        role.getAuditSection().setDelF("1");
        this.roleRepository.save(role);
        //act/assert
        mockMvc.perform(get(path + "/roles/{roleId}", role.getId()).headers(headers)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional//used only in this method to make test run
    void shouldUpdateRoleSuccessfully() throws Exception {
        this.setToken(UserType.ADMIN);
        //arrange
        //allperms
        Arrays.asList("add_user", "edit_user", "list_stacks", "edit_stack", "add_stack").forEach(permission -> {
            this.permissionRepository.save(new Permission(permission));
        });
        //role
        Role role = TestModels.role("User Manager", RoleType.ADMIN);
        role.setDescription("manage users");
        Collection<String> definedAssignedPerms = Arrays.asList("add_user", "edit_stack", "edit_user");
        definedAssignedPerms.forEach(permission -> {
            //transactional added to make this work
            role.addPermission(this.permissionRepository.findByPermission(permission).get());
        });
        role.setRoleType(RoleType.ADMIN);
        this.roleRepository.save(role);

        //act
        // input
        RoleRequestDto dto = new RoleRequestDto();
        dto.setName("Packages Manager");
        dto.setDescription("Manages packages");
        Collection<String> newRolePerms = Arrays
                .asList("list_stacks", "edit_stack", "add_stack");
        dto.setPermissions(newRolePerms);
        dto.setRoleType(RoleType.CLIENT.name());

        mockMvc.perform(put(path + "/roles/{roleId}", role.getId()).headers(headers).contentType("application/json")
                .content(TestUtils.asJsonString(dto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resource updated successfully")));
        //assert
        Role definedRole = this.roleRepository.findById(role.getId()).get();
        assertEquals("Packages Manager", role.getName());
        assertEquals("Manages packages", role.getDescription());
        assertEquals(RoleType.CLIENT, role.getRoleType());

        //perms
        assertEquals(3, definedRole.getPermissions().size());
        assertEquals(role.getPermissions().size(), definedRole.getPermissions().size());
        assertTrue(role.getPermissions().stream().map(Permission::getPermission).collect(Collectors.toList()).containsAll(newRolePerms));
    }

    /*
      an admin user should be able to update client type roles
     */
    @Test
    @Transactional//used only in this method to make test run
    void shouldUpdateClientTypeRolesByAdminUserSuccessfully() throws Exception {
        this.setToken(UserType.ADMIN);
        //arrange
        //allperms
        Arrays.asList("add_user", "edit_user", "list_stacks", "edit_stack", "add_stack").forEach(permission -> {
            this.permissionRepository.save(new Permission(permission));
        });
        //role
        Role role = TestModels.role("User Manager", RoleType.CLIENT);
        role.setDescription("manage users");
        Collection<String> definedAssignedPerms = Arrays.asList("add_user", "edit_stack", "edit_user");
        definedAssignedPerms.forEach(permission -> {
            //transactional added to make this work
            role.addPermission(this.permissionRepository.findByPermission(permission).get());
        });
        role.setRoleType(RoleType.CLIENT);
        this.roleRepository.save(role);

        //act
        // input
        RoleRequestDto dto = new RoleRequestDto();
        dto.setName("Packages Manager");
        dto.setDescription("Manages packages");
        Collection<String> newRolePerms = Arrays
                .asList("list_stacks", "edit_stack", "add_stack");
        dto.setPermissions(newRolePerms);
        dto.setRoleType(RoleType.CLIENT.name());

        mockMvc.perform(put(path + "/roles/{roleId}", role.getId()).headers(headers).contentType("application/json")
                .content(TestUtils.asJsonString(dto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Is.is("Resource updated successfully")));
        //assert
        Role definedRole = this.roleRepository.findById(role.getId()).get();
        assertEquals("Packages Manager", role.getName());
        assertEquals("Manages packages", role.getDescription());
        assertEquals(RoleType.CLIENT, role.getRoleType());
    }

    @Test
    void shouldSoftDeleteRoleSuccessfully() throws Exception {
        this.setToken(UserType.ADMIN);
        //arrange
        Role role = TestModels.role("User Manager", RoleType.ADMIN);
        role.setDescription("manage users");
        this.roleRepository.save(role);
        //act
        mockMvc.perform(delete(path + "/roles/{roleId}", role.getId()).headers(headers).contentType("application/json"))
                .andExpect(status().isNoContent());
        //assert
        Optional<Role> optional = this.roleRepository.findById(role.getId());
        assertTrue(optional.isEmpty());
        role = (Role) em.createNativeQuery("select * from role where id=:id", Role.class)
                .setParameter("id", role.getId())
                .getSingleResult();
        assertEquals("1", role.getAuditSection().getDelF());
    }


}