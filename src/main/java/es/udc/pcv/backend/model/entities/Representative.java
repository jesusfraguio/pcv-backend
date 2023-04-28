package es.udc.pcv.backend.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "\"Representative\"")
//@PrimaryKeyJoinColumn(name = "\"userId\"", referencedColumnName = "id")
public class Representative extends User {
  private String name;
  private String surname;
  private String phone;
  private Entidad entity;

  public Representative(String name, String surname, String phone, Entidad entity) {
    this.name = name;
    this.surname = surname;
    this.phone = phone;
    this.entity = entity;
  }

  public Representative(User user, String name, String surname, String phone, Entidad entity) {
    super(user.getPassword(),user.getEmail(),user.getRole());
    this.setName(name);
    this.setSurname(surname);
    this.setPhone(phone);
    this.entity = entity;
  }

  public Representative() {
  }

  @Column(name = "\"name\"")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "\"surname\"")
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @Column(name = "\"phone\"",nullable = false)
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @OneToOne(fetch = FetchType.LAZY,optional = false)
  @JoinColumn(name = "\"entityId\"")
  public Entidad getEntity() {
    return entity;
  }

  public void setEntity(Entidad entity) {
    this.entity = entity;
  }
}

