package com.example.englishmaster_be.Model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;


@Entity
@Table(name = "roles")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_discription")
    private String roleDiscription;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Collection<User> users;

    public Role() {
    }

	public UUID getRoleId() {
		return roleId;
	}

	public void setRoleId(UUID roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDiscription() {
		return roleDiscription;
	}

	public void setRoleDiscription(String roleDiscription) {
		this.roleDiscription = roleDiscription;
	}

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}
    

}
