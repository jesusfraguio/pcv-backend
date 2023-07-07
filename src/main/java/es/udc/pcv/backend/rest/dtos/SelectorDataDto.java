package es.udc.pcv.backend.rest.dtos;

/*
Performance class that might be anything and only has id and value
 */
public class SelectorDataDto {
  private Long id;
  private String name;

  public SelectorDataDto(Long id, String name) {
    this.id = id;
    this.name = name;
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
}
