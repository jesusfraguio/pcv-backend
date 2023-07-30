package es.udc.pcv.backend.rest.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EmailDto {
  @NotNull
  @Size(min = 3, max = 110)
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
