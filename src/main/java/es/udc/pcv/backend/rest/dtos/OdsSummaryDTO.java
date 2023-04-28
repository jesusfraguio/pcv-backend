package es.udc.pcv.backend.rest.dtos;

public class OdsSummaryDTO {
  private long id;
  private int number;
  private String name;

  public OdsSummaryDTO() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
