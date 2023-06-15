package es.udc.pcv.backend.rest.dtos;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDataDto {
  public interface AllValidations {}

  private Long id;
  @NotNull(groups={VolunteerDataDto.AllValidations.class})
  @Size(min=1, max=60, groups={VolunteerDataDto.AllValidations.class})
  private String name;
  @NotNull(groups={VolunteerDataDto.AllValidations.class})
  @Size(min=1, max=60, groups={VolunteerDataDto.AllValidations.class})
  private String surname;
  @NotNull(groups={VolunteerDataDto.AllValidations.class})
  @Size(min=8, max=22, groups={VolunteerDataDto.AllValidations.class})
  private String dni;
  private LocalDate dniExpiration;
  @NotNull(groups={VolunteerDataDto.AllValidations.class})
  @Size(min=1, max=60, groups={VolunteerDataDto.AllValidations.class})
  private String locality;
  @NotNull(groups={VolunteerDataDto.AllValidations.class})
  @Size(min=1, max=15, groups={VolunteerDataDto.AllValidations.class})
  private String phone;
  @NotNull(groups={VolunteerDataDto.AllValidations.class})
  private LocalDate birth;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  public LocalDate getDniExpiration() {
    return dniExpiration;
  }

  public void setDniExpiration(LocalDate dniExpiration) {
    this.dniExpiration = dniExpiration;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public LocalDate getBirth() {
    return birth;
  }

  public void setBirth(LocalDate birth) {
    this.birth = birth;
  }
}
