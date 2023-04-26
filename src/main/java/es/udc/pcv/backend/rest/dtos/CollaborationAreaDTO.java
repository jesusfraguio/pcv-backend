package es.udc.pcv.backend.rest.dtos;

public class CollaborationAreaDTO {
  private long id;
  private String name;

  public CollaborationAreaDTO() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
