package es.udc.pcv.backend.rest.dtos;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProjectDto {

  public interface AllValidations {}

  private Long id;
  @NotNull(groups={ProjectDto.AllValidations.class})
  @Size(min=1, max=63, groups={ProjectDto.AllValidations.class})
  private String name;
  @NotNull(groups={ProjectDto.AllValidations.class})
  @Size(min=1, max=125, groups={ProjectDto.AllValidations.class})
  private String shortDescription;
  @NotNull(groups={ProjectDto.AllValidations.class})
  @Size(min=1, max=480, groups={ProjectDto.AllValidations.class})
  private String longDescription;
  @NotNull(groups={ProjectDto.AllValidations.class})
  @Size(min=1, max=60, groups={ProjectDto.AllValidations.class})
  private String locality;
  @NotNull(groups={ProjectDto.AllValidations.class})
  @Size(min=1, max=60, groups={ProjectDto.AllValidations.class})
  private String schedule;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private int capacity;
  @NotNull(groups={ProjectDto.AllValidations.class})
  @Size(min=1, max=60, groups={ProjectDto.AllValidations.class})
  private String preferableVolunteer;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private boolean areChildren;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private boolean isVisible;
  @NotEmpty(groups={ProjectDto.AllValidations.class})
  private List<@Size(min=1, max=220, groups={ProjectDto.AllValidations.class}) String> tasks;
  @NotEmpty(groups={ProjectDto.AllValidations.class})
  private List<Long> ods;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private Long entityId;
  @NotNull(groups={ProjectDto.AllValidations.class})
  private Long areaId;

  public ProjectDto() {
  }

  public ProjectDto(Long id, String name, String shortDescription, String longDescription,
                    String locality, String schedule, int capacity,
                    String preferableVolunteer, boolean areChildren, boolean isVisible,
                    List<@Size(min = 1, max = 220, groups = {
                        AllValidations.class}) String> tasks, List<Long> ods, Long entityId,
                    Long areaId) {
    this.id = id;
    this.name = name;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.locality = locality;
    this.schedule = schedule;
    this.capacity = capacity;
    this.preferableVolunteer = preferableVolunteer;
    this.areChildren = areChildren;
    this.isVisible = isVisible;
    this.tasks = tasks;
    this.ods = ods;
    this.entityId = entityId;
    this.areaId = areaId;
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

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public String getSchedule() {
    return schedule;
  }

  public void setSchedule(String schedule) {
    this.schedule = schedule;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public String getPreferableVolunteer() {
    return preferableVolunteer;
  }

  public void setPreferableVolunteer(String preferableVolunteer) {
    this.preferableVolunteer = preferableVolunteer;
  }

  public boolean isAreChildren() {
    return areChildren;
  }

  public void setAreChildren(boolean areChildren) {
    this.areChildren = areChildren;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean visible) {
    isVisible = visible;
  }

  public List<String> getTasks() {
    return tasks;
  }

  public void setTasks(List<String> tasks) {
    this.tasks = tasks;
  }

  public List<Long> getOds() {
    return ods;
  }

  public void setOds(List<Long> ods) {
    this.ods = ods;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }
}
