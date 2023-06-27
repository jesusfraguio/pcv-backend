package es.udc.pcv.backend.model.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "\"Project\"")
public class Project {

  private Long id;
  private String name;
  private String shortDescription;
  private String longDescription;
  private String locality;
  private String schedule;
  private int capacity;
  private String preferableVolunteer;
  private LocalDate completenessDate;
  private boolean areChildren;
  private boolean isPaused;
  private boolean isVisible;
  private boolean isDeleted;
  private Entidad entity;
  private CollaborationArea collaborationArea;
  private List<Task> tasks = new ArrayList<>();
  private Set<Ods> ods = new HashSet<>();

  public Project() {
  }

  public Project(String name, String shortDescription, String longDescription,
                 String locality, String schedule, int capacity, String preferableVolunteer, boolean areChildren,
                 boolean isVisible, boolean isPaused, Entidad entity, CollaborationArea collaborationArea) {
    this.name = name;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.locality = locality;
    this.schedule = schedule;
    this.capacity = capacity;
    this.preferableVolunteer = preferableVolunteer;
    this.completenessDate = null;
    this.areChildren = areChildren;
    this.isPaused = isPaused;
    this.isVisible = isVisible;
    this.isDeleted = false;
    this.entity = entity;
    this.collaborationArea = collaborationArea;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "\"name\"")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "\"shortDescription\"")
  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  @Column(name = "\"longDescription\"")
  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  @Column(name = "\"locality\"")
  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  @Column(name = "\"schedule\"")
  public String getSchedule() {
    return schedule;
  }

  public void setSchedule(String schedule) {
    this.schedule = schedule;
  }

  @Column(name = "\"capacity\"")
  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  @Column(name = "\"preferableVolunteer\"")
  public String getPreferableVolunteer() {
    return preferableVolunteer;
  }

  public void setPreferableVolunteer(String preferableVolunteer) {
    this.preferableVolunteer = preferableVolunteer;
  }

  @Column(name = "\"completenessDate\"")
  public LocalDate getCompletenessDate() {
    return completenessDate;
  }

  public void setCompletenessDate(LocalDate completenessDate) {
    this.completenessDate = completenessDate;
  }

  @Column(name = "\"areChildren\"")
  public boolean isAreChildren() {
    return areChildren;
  }

  public void setAreChildren(boolean areChildren) {
    this.areChildren = areChildren;
  }

  @Column(name = "\"isPaused\"")
  public boolean isPaused() {
    return isPaused;
  }

  public void setPaused(boolean paused) {
    isPaused = paused;
  }

  @Column(name = "\"isVisible\"")
  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean visible) {
    isVisible = visible;
  }

  @Column(name = "\"isDeleted\"")
  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = false)
  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }

  @ManyToMany
  @JoinTable(
      name = "\"Project_Ods\"",
      joinColumns = @JoinColumn(name = "\"projectId\""),
      inverseJoinColumns = @JoinColumn(name = "\"odsId\"")
  )
  public Set<Ods> getOds() {
    return ods;
  }

  public void setOds(Set<Ods> ods) {
    this.ods = ods;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"entityId\"")
  public Entidad getEntity() {
    return entity;
  }

  public void setEntity(Entidad entity) {
    this.entity = entity;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"collaborationAreaId\"")
  public CollaborationArea getCollaborationArea() {
    return collaborationArea;
  }

  public void setCollaborationArea(CollaborationArea collaborationArea) {
    this.collaborationArea = collaborationArea;
  }
}
