package es.udc.pcv.backend.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "\"Entity\"")
public class Entidad {
  private long id;
  private String name;
  private String shortDescription;
  private String url;
  private String address;
  private String email;
  private String phone;
  private File certFile;
  private File logo;

  public Entidad() {
  }

  public Entidad(String name, String shortDescription, String url, String address,
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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Column(name = "\"name\"")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "\"shortDescription\"")
  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  @Column(name = "\"url\"")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "\"address\"")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Column(name = "\"email\"")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "\"phone\"")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @OneToOne(fetch = FetchType.LAZY,optional = true)
  @JoinColumn(name = "\"certFileId\"")
  public File getCertFile() {
    return certFile;
  }

  public void setCertFile(File certFile) {
    this.certFile = certFile;
  }

  @OneToOne(fetch = FetchType.LAZY,optional = true)
  @JoinColumn(name = "\"logoId\"")
  public File getLogo() {
    return logo;
  }

  public void setLogo(File logo) {
    this.logo = logo;
  }
}
