package es.udc.pcv.backend.rest.dtos;

import javax.validation.constraints.NotNull;

public class ParticipationDto {
  public interface AllValidations {}
  private Long id;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private boolean isRecommended;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private Long projectId;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private Long volunteerId;
  private String status;

  public ParticipationDto(boolean isRecommended, Long projectId, Long volunteerId) {
    this.isRecommended = isRecommended;
    this.projectId = projectId;
    this.volunteerId = volunteerId;
  }

  public ParticipationDto() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isRecommended() {
    return isRecommended;
  }

  public void setRecommended(boolean recommended) {
    isRecommended = recommended;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Long getVolunteerId() {
    return volunteerId;
  }

  public void setVolunteerId(Long volunteerId) {
    this.volunteerId = volunteerId;
  }
}
