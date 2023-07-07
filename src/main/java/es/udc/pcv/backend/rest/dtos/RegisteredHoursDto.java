package es.udc.pcv.backend.rest.dtos;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class RegisteredHoursDto {
  public interface AllValidations {}

  private Long id;

  @NotNull
  private Long participationId;
  @NotNull
  @PositiveOrZero
  private Integer hours;
  @NotNull
  private LocalDate date;
  private String volunteerName;
  private Long volunteerId;
  //projectName not required since usually 1 volunteer only belongs to 1 project

  public RegisteredHoursDto() {
  }

  public RegisteredHoursDto(Long id, Long participationId, Integer hours, LocalDate date, String volunteerName, Long volunteerId) {
    this.id = id;
    this.participationId = participationId;
    this.hours = hours;
    this.date = date;
    this.volunteerName = volunteerName;
    this.volunteerId = volunteerId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getParticipationId() {
    return participationId;
  }

  public void setParticipationId(Long participationId) {
    this.participationId = participationId;
  }

  public Integer getHours() {
    return hours;
  }

  public void setHours(Integer hours) {
    this.hours = hours;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getVolunteerName() {
    return volunteerName;
  }

  public void setVolunteerName(String volunteerName) {
    this.volunteerName = volunteerName;
  }

  public Long getVolunteerId() {
    return volunteerId;
  }

  public void setVolunteerId(Long volunteerId) {
    this.volunteerId = volunteerId;
  }
}
