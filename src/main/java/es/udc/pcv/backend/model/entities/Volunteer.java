package es.udc.pcv.backend.model.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "\"VolunteerRecord\"")
public class Volunteer {
    private Long id;
    private String name;
    private String surname;
    private String dni;
    private LocalDate dniExpiration;
    private String locality;
    private String phone;
    private LocalDate birth;
    private boolean isDeleted;
    private User user;

    public Volunteer() {
    }

  public Volunteer(String name, String surname, String locality, String phone,
                   LocalDate birth) {
    this.name = name;
    this.surname = surname;
    this.locality = locality;
    this.phone = phone;
    this.birth = birth;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  @Column(name = "\"dni\"")
  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  @Column(name = "\"dniExpiration\"")
  public LocalDate getDniExpiration() {
    return dniExpiration;
  }

  public void setDniExpiration(LocalDate dniExpiration) {
    this.dniExpiration = dniExpiration;
  }

  @Column(name = "\"locality\"")
  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  @Column(name = "\"phone\"")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Column(name = "\"birth\"")
  public LocalDate getBirth() {
    return birth;
  }

  public void setBirth(LocalDate birth) {
    this.birth = birth;
  }

  @Column(name = "\"isDeleted\"")
  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  @OneToOne(fetch = FetchType.LAZY,optional = true)
  @JoinColumn(name = "\"userId\"")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
