package es.udc.pcv.backend.rest.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RepresentativeDto {
    public interface AllValidations {}
    private Long id;
    private String password;
    @NotNull
    @Size(min=1, max=60, groups={RepresentativeDto.AllValidations.class})
    @Email(groups={RepresentativeDto.AllValidations.class})
    private String email;
    private String role;
    @NotNull(groups={RepresentativeDto.AllValidations.class})
    @Size(min=1, max=60, groups={RepresentativeDto.AllValidations.class})
    private String name;
    @NotNull(groups={RepresentativeDto.AllValidations.class})
    @Size(min=1, max=60, groups={RepresentativeDto.AllValidations.class})
    private String surname;
    @NotNull(groups={RepresentativeDto.AllValidations.class})
    @Size(min=1, max=15, groups={RepresentativeDto.AllValidations.class})
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
