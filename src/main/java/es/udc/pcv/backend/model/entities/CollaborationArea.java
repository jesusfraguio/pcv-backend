package es.udc.pcv.backend.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "\"CollaborationArea\"")
public class CollaborationArea {
  private Long id;
  private String name;

  public CollaborationArea() {
  }

  public CollaborationArea(String name) {
    this.name = name;
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

}
