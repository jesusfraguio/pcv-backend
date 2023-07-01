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

  public RegisteredHoursDto() {
  }

  public RegisteredHoursDto(Long id, Long participationId, Integer hours, LocalDate date) {
    this.id = id;
    this.participationId = participationId;
    this.hours = hours;
    this.date = date;
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
}
