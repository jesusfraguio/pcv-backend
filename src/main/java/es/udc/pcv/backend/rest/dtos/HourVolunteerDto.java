package es.udc.pcv.backend.rest.dtos;

import javax.validation.constraints.NotNull;

public class HourVolunteerDto {
  @NotNull
  private Long volunteerId;
  private Integer totalHours;

  public HourVolunteerDto() {}

  public HourVolunteerDto(Long volunteerId, Integer totalHours) {
    this.volunteerId = volunteerId;
    this.totalHours = totalHours;
  }

  public Long getVolunteerId() {
    return volunteerId;
  }

  public void setVolunteerId(Long volunteerId) {
    this.volunteerId = volunteerId;
  }

  public Integer getTotalHours() {
    return totalHours;
  }

  public void setTotalHours(Integer totalHours) {
    this.totalHours = totalHours;
  }
}
