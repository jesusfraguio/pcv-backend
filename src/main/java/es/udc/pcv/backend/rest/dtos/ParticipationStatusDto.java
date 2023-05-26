package es.udc.pcv.backend.rest.dtos;

public class ParticipationStatusDto {
  private String status;

  public ParticipationStatusDto(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
