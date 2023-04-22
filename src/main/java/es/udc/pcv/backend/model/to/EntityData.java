package es.udc.pcv.backend.model.to;

import es.udc.pcv.backend.model.entities.File;

public class EntityData {
  private long id;
  private String name;
  private String shortDescription;
  private String url;
  private String address;
  private String email;
  private String phone;
  private File certFile;
  private File logo;

  public EntityData(String name, String shortDescription, String url, String address,
                    String email, String phone, File certFile,
                    File logo) {
    this.name = name;
    this.shortDescription = shortDescription;
    this.url = url;
    this.address = address;
    this.email = email;
    this.phone = phone;
    this.certFile = certFile;
    this.logo = logo;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
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

  public File getCertFile() {
    return certFile;
  }

  public void setCertFile(File certFile) {
    this.certFile = certFile;
  }

  public File getLogo() {
    return logo;
  }

  public void setLogo(File logo) {
    this.logo = logo;
  }
}
