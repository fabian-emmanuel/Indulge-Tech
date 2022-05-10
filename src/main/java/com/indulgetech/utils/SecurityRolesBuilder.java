package com.indulgetech.utils;



import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.roles.RoleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for building security roles and permissions
 */
public class SecurityRolesBuilder {
	
	private List<Role> roles = new ArrayList<>();
	private Role lastRole = null;
	
	
	public SecurityRolesBuilder addRole(String name,String roleKey, RoleType type) {
		return this.add(name, roleKey, type);
	}

	public SecurityRolesBuilder addRole(String name, RoleType type) {
		return this.add(name,"", type);
	}

	private SecurityRolesBuilder add(String name,String roleKey, RoleType type) {

		Role role = new Role();
		role.setName(name);
		role.setRoleKey(roleKey);
		role.setRoleType(type);
		role.setSystemCreated(true);
		roles.add(role);
		this.lastRole = role;

		return this;
	}

	public SecurityRolesBuilder addPermission(String name) {
		if(this.lastRole == null) {
			Role role = this.roles.get(0);
			if(role == null) {
				role = new Role();
			    role.setName("UNDEFINED");
				role.setRoleType(RoleType.ADMIN);
				role.setSystemCreated(true);
				roles.add(role);
				this.lastRole = role;
			}
		}
		
		Permission permission = new Permission();
		permission.setName(name);
		lastRole.addPermission(permission);
		
		return this;
	}
	
	public SecurityRolesBuilder addPermission(Permission permission) {
		
		if(this.lastRole == null) {
			Role role = this.roles.get(0);
			if(role == null) {
				role = new Role();
				role.setName("UNDEFINED");
				role.setRoleType(RoleType.ADMIN);
				role.setSystemCreated(true);
				roles.add(role);
				this.lastRole = role;
			}
		}
		if(lastRole!=null) {
			lastRole.addPermission(permission);
		}
		return this;
	}
	
	public List<Role> build() {
		return roles;
	}

	public Role getLastRole(){
		return lastRole;
	}

}
