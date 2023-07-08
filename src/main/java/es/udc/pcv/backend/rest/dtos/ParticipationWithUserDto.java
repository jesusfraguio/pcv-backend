package es.udc.pcv.backend.rest.dtos;

public class ParticipationWithUserDto {
  private Long id;
  private Long projectId;
  private String projectName;
  private Long volunteerId;
  private String volunteerName;
  private String volunteerSurname;
  private Integer totalHours;
  private String status;

  public ParticipationWithUserDto(Long id, Long projectId, String projectName,
                                  Long volunteerId, String volunteerName, String volunteerSurname,
                                  Integer totalHours, String status) {
    this.id = id;
    this.projectId = projectId;
    this.projectName = projectName;
    this.volunteerId = volunteerId;
    this.volunteerName = volunteerName;
    this.volunteerSurname = volunteerSurname;
    this.totalHours = totalHours;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getVolunteerName() {
    return volunteerName;
  }

  public void setVolunteerName(String volunteerName) {
    this.volunteerName = volunteerName;
  }

  public String getVolunteerSurname() {
    return volunteerSurname;
  }

  public void setVolunteerSurname(String volunteerSurname) {
    this.volunteerSurname = volunteerSurname;
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
