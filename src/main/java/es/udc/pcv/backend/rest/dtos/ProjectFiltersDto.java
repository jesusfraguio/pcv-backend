package es.udc.pcv.backend.rest.dtos;

import javax.validation.constraints.Size;

public class ProjectFiltersDto {
  @Size(max=100)
  private String name;
  @Size(max=100)
  private String locality;
  private Long collaborationAreaId;
  //private Long entityId

  public ProjectFiltersDto() {
  }

  public ProjectFiltersDto(String name, String locality, Long collaborationAreaId) {
    this.name = name;
    this.locality = locality;
    this.collaborationAreaId = collaborationAreaId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public Long getCollaborationAreaId() {
    return collaborationAreaId;
  }

  public void setCollaborationAreaId(Long collaborationAreaId) {
    this.collaborationAreaId = collaborationAreaId;
  }
}
