package es.udc.pcv.backend.rest.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class EntityDto {
  public interface AllValidations {}

  private Long id;
  @NotNull
  @Size(min=1, max=60, groups={EntityDto.AllValidations.class})
  private String name;
  @Size(min=1, max=240, groups={EntityDto.AllValidations.class})
  private String shortDescription;
  @Size(min=0, max=60, groups={EntityDto.AllValidations.class})
  @Email(groups={EntityDto.AllValidations.class})
  private String email;
  @Size(min=0, max=60, groups={EntityDto.AllValidations.class})
  private String url;
  @NotNull
  @Size(min=1, max=60, groups={EntityDto.AllValidations.class})
  private String address;
  @Size(min=0, max=31, groups={EntityDto.AllValidations.class})
  private String phone;

  private String certName;
  private String logoName;

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

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getCertName() {
    return certName;
  }

  public void setCertName(String certName) {
    this.certName = certName;
  }

  public String getLogoName() {
    return logoName;
  }

  public void setLogoName(String logoName) {
    this.logoName = logoName;
  }
}
