package es.udc.pcv.backend.rest.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProjectSummaryDto {
  private Long id;
  @NotNull(groups={ProjectDto.AllValidations.class})
  @Size(min=1, max=63, groups={ProjectDto.AllValidations.class})
  private String name;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private Long entityId;
  private String entityName;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private Long areaId;
  private String areaName;

  public ProjectSummaryDto() {
  }

  public ProjectSummaryDto(Long id, String name, Long entityId, String entityName,
                           Long areaId, String areaName) {
    this.id = id;
    this.name = name;
    this.entityId = entityId;
    this.entityName = entityName;
    this.areaId = areaId;
    this.areaName = areaName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }
}
