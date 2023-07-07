package es.udc.pcv.backend.model.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "\"RegisteredHours\"")
public class RegisteredHours {
  private Long id;
  private Integer hours;
  private LocalDate date;
  private Participation participation;

  public RegisteredHours(Integer hours, LocalDate date, Participation participation) {
    this.hours = hours;
    this.date = date;
    this.participation = participation;
  }

  public RegisteredHours() {

  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "\"hours\"")
  public Integer getHours() {
    return hours;
  }

  public void setHours(Integer hours) {
    this.hours = hours;
  }

  @Column(name = "\"date\"")
  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"participationId\"")
  public Participation getParticipation() {
    return participation;
  }

  public void setParticipation(Participation participation) {
    this.participation = participation;
  }
}
