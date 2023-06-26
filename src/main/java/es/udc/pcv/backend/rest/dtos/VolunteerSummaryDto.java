package es.udc.pcv.backend.rest.dtos;

public class VolunteerSummaryDto {
  private Long volunteerId;
  private String email;
  private String phone;
  private Boolean isVerified;
  private String name;
  private String surname;
  private Boolean hasCertFile;
  private Boolean hasHarassmentFile;
  private Boolean hasDniFile;
  private Boolean hasPhotoFile;

  public VolunteerSummaryDto(Long volunteerId, String email, String phone,
                             Boolean isVerified, String name, String surname, Boolean hasCertFile, Boolean hasHarassmentFile,
                             Boolean hasDniFile, Boolean hasPhotoFile) {
    this.volunteerId = volunteerId;
    this.email = email;
    this.phone = phone;
    this.isVerified = isVerified;
    this.name = name;
    this.surname = surname;
    this.hasCertFile = hasCertFile;
    this.hasHarassmentFile = hasHarassmentFile;
    this.hasDniFile = hasDniFile;
    this.hasPhotoFile = hasPhotoFile;
  }

  public Long getVolunteerId() {
    return volunteerId;
  }

  public void setVolunteerId(Long volunteerId) {
    this.volunteerId = volunteerId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Boolean getVerified() {
    return isVerified;
  }

  public void setVerified(Boolean verified) {
    isVerified = verified;
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

  public Boolean getHasCertFile() {
    return hasCertFile;
  }

  public void setHasCertFile(Boolean hasCertFile) {
    this.hasCertFile = hasCertFile;
  }

  public Boolean getHasHarassmentFile() {
    return hasHarassmentFile;
  }

  public void setHasHarassmentFile(Boolean hasHarassmentFile) {
    this.hasHarassmentFile = hasHarassmentFile;
  }

  public Boolean getHasDniFile() {
    return hasDniFile;
  }

  public void setHasDniFile(Boolean hasDniFile) {
    this.hasDniFile = hasDniFile;
  }

  public Boolean getHasPhotoFile() {
    return hasPhotoFile;
  }

  public void setHasPhotoFile(Boolean hasPhotoFile) {
    this.hasPhotoFile = hasPhotoFile;
  }
}
