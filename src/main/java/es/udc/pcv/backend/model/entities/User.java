package es.udc.pcv.backend.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "\"User\"")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

	public enum RoleType {USER,ADMIN,REPRESENTATIVE};

	private Long id;
	private String password;
	private String email;
    private RoleType role;

	public User() {}

	public User(String password, String email) {
		this.password = password;
		this.email = email;
	}
	public User(String password, String email, RoleType role) {
		this.password = password;
		this.email = email;
		this.role = role;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "\"password\"")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "\"email\"")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "\"role\"")
	public RoleType getRole() {
		return role;
	}

	public void setRole(RoleType role) {
		this.role = role;
	}

}
