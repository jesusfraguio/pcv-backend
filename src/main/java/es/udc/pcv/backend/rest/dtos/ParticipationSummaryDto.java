package es.udc.pcv.backend.rest.dtos;

public class ParticipationSummaryDto {
  private Long id;
  private boolean isRecommended;
  private Long projectId;
  private String projectName;
  private Long volunteerId;
  private Integer totalHours;
  private String status;

  public ParticipationSummaryDto(Long id, boolean isRecommended, Long projectId,
                                 String projectName, Long volunteerId, Integer totalHours,
                                 String status) {
    this.id = id;
    this.isRecommended = isRecommended;
    this.projectId = projectId;
    this.projectName = projectName;
    this.volunteerId = volunteerId;
    this.totalHours = totalHours;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
