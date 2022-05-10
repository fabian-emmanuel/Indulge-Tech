package com.indulgetech.services.init;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.security.admin.AdminUserDetailsService;
import com.indulgetech.services.system.SystemConfigurationService;
import com.indulgetech.services.user.admin.PermissionService;
import com.indulgetech.services.user.admin.RoleService;
import com.indulgetech.utils.SecurityRolesBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

import static com.indulgetech.constants.Constants.*;
import static com.indulgetech.models.users.roles.RoleType.ADMIN;
import static com.indulgetech.models.users.roles.RoleType.CLIENT;

@RequiredArgsConstructor
@Slf4j
@Service("initializationDatabase")
public class InitializationDatabaseImpl implements InitializationDatabase {


//    private final CountryService countryService;
    private final RoleService roleService;
    private final AdminUserDetailsService adminUserDetailsService;
    private final PermissionService permissionService;
    private final SystemConfigurationService systemConfigurationService;

    private String name;

    public boolean isEmpty() {
        return roleService.count() == 0;
    }

    @Transactional
    public void populate(String contextName) {
        this.name = contextName;
        createRoles();
        createDefaultAdmin();
        createConfigurations();
    }


    private void createDefaultAdmin() {
        adminUserDetailsService.createDefaultAdmin();
    }


    private void createRoles() {

        log.info(String.format("%s : Creating Security groups ", name));

        Collection<Permission> definedPermissions = this.permissionService.fetchPermissions();
        Map<String, Permission> definedPermsMap = this.mapByPermission(definedPermissions);

        //get system permissions from json file
        Collection<Permission> systemPermissions = this.loadSystemPermissions("reference/permissions.json");

        for (Permission permission : systemPermissions) {
            if (!definedPermsMap.containsKey(permission.getPermission())) {
                definedPermsMap.put(permission.getPermission(), this.createPermission(permission));
            }
        }

        SecurityRolesBuilder roleBuilder = new SecurityRolesBuilder();
        Optional<Role> optionalRole = roleService.fetchByRoleKey(ROLE_SUPER_ADMIN);
        Role superAdminRole = optionalRole.orElseGet(() -> roleBuilder.addRole("SUPER ADMINISTRATOR", ROLE_SUPER_ADMIN, ADMIN).getLastRole());

        //default admin member role
        optionalRole = roleService.fetchByRoleKey(ROLE_ADMIN_MEMBER);
        Role adminMemberRole = optionalRole.orElseGet(() -> roleBuilder.addRole("ADMIN MEMBER", ROLE_ADMIN_MEMBER, ADMIN).getLastRole());
        adminMemberRole.addPermission(definedPermsMap.get("adminMemberAccess"));

        //TODO : Add Permissions
        Collection<String> clientPermissions = List.of();


        //ORG MEMBER
        optionalRole = roleService.fetchByRoleKey(ROLE_CLIENT_MEMBER);
        Role clientMemberRole = optionalRole.orElseGet(() -> roleBuilder.addRole("CLIENT MEMBER", ROLE_CLIENT_MEMBER, CLIENT).getLastRole());

        //add role permissions
        for (Permission permission : definedPermsMap.values()) {
            String permissionKey = permission.getPermission();
            superAdminRole.addPermission(permission);
            if (clientPermissions.contains(permissionKey)) {
                clientMemberRole.addPermission(definedPermsMap.get(permissionKey));
            }
        }

        for (Role g : roleBuilder.build()) {
            roleService.create(g);
        }

    }

    private Collection<Permission> loadSystemPermissions(String jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFilePath);
            return mapper.readValue(in, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new ServiceException(e.toString());
        }
    }

    /**
     * map collection of permission entities by permission as key and permission entity as value
     *
     * @param permissions
     * @return
     */
    private Map<String, Permission> mapByPermission(Collection<Permission> permissions) {
        Map<String, Permission> permissionMap = new HashMap<>();
        permissions.forEach(permission -> permissionMap.put(permission.getPermission(), permission));
        return permissionMap;
    }


    private Permission createPermission(Permission perm) {
        permissionService.create(perm);
        return perm;
    }


//    private void createCountries() {
//        if (countryService.count() <= 0) {
//            log.info(String.format("%s : Populating Countries ", name));
//            HashMap<String, Locale> locales=SchemaConstant.getLocales();
//            for (String code : SchemaConstant.getCountryIsoCode()) {
//                Locale locale = locales.get(code);
//                if (locale != null) {
//                    Country country = new Country();
//                    country.setIsoCode(code);
//                    String name = locale.getDisplayCountry(new Locale("en"));
//                    country.setName(name);
//                    countryService.create(country);
//                }
//            }
//        }
//    }

//    private Map<String, Country> mapCountriesByCode(Collection<Country> countries) {
//        Map<String, Country> map = new HashMap<>();
//        countries.forEach(country -> map.put(country.getIsoCode(), country));
//        return map;
//    }


//    private void createCurrencies() {
//
//        if (currencyService.count() <= 0) {
//
//            log.info(String.format("%s : Populating Currencies ", name));
//
//            HashMap<String, String> currenciesMap=SchemaConstant.getCurrenciesMap();
//
//            for (String code : currenciesMap.keySet()) {
//
//                try {
//                    Currency c = Currency.getInstance(code);
//
//                    if (c == null) {
//                        log.info(String.format("%s : Populating Currencies : no currency for code : %s", name, code));
//                    }
//                    //check if it exist
//                    com.decagon.fellowship.models.reference.currency.Currency currency = new Currency();
//                    currency.setName(c.getCurrencyCode());
//                    currency.setCurrency(c);
//                    currencyService.create(currency);
//
//                    //System.out.println(l.getCountry() + "   " + c.getSymbol() + "  " + c.getSymbol(l));
//                } catch (IllegalArgumentException e) {
//                    log.info(String.format("%s : Populating Currencies : no currency for code : %s", name, code));
//                }
//            }
//        }
//
//    }

//    private Map<String, Currency> mapCurrenciesByCode(Collection<Currency> currencies) {
//        Map<String, Currency> map = new HashMap<>();
//        currencies.forEach(currency -> map.put(currency.getCode(), currency));
//        return map;
//    }


    private void createConfigurations() {

        log.info(String.format("%s : Creating default configurations ", name));
        this.systemConfigurationService.createDefaultConfigurations();
    }


}
