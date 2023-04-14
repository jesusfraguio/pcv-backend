package es.udc.pcv.backend.rest.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewPasswordParamsDto {

  private String newPassword;

  public NewPasswordParamsDto() {}

  @NotNull
  @Size(min=9, max=60)
  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

}
