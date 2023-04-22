package es.udc.pcv.backend.model.to;

public class UserWithRepresentative {
  private long id;
  private String password;
  private String email;
  private String name;
  private String surname;
  private String phone;
  private long entityId;

  public UserWithRepresentative(String password, String email,
                                String name, String surname,
                                String phone, long entityId) {
    this.password = password;
    this.email = email;
    this.name = name;
    this.surname = surname;
    this.phone = phone;
    this.entityId = entityId;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
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

  public long getEntityId() {
    return entityId;
  }

  public void setEntityId(long entityId) {
    this.entityId = entityId;
  }
}
