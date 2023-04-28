package es.udc.pcv.backend.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "\"Task\"")
public class Task {
  private Long id;
  private String name;
  private Project project;

  public Task() {
  }

  public Task(String name, Project project) {
    this.name = name;
    this.project = project;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"projectId\"")
  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }
}
