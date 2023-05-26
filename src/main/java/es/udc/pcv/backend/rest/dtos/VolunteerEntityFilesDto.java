package es.udc.pcv.backend.rest.dtos;

public class VolunteerEntityFilesDto {
  private boolean hasCertFile;
  private boolean hasHarassmentFile;

  public VolunteerEntityFilesDto(boolean hasCertFile, boolean hasHarassmentFile) {
    this.hasCertFile = hasCertFile;
    this.hasHarassmentFile = hasHarassmentFile;
  }

  public boolean isHasCertFile() {
    return hasCertFile;
  }

  public void setHasCertFile(boolean hasCertFile) {
    this.hasCertFile = hasCertFile;
  }

  public boolean isHasHarassmentFile() {
    return hasHarassmentFile;
  }

  public void setHasHarassmentFile(boolean hasHarassmentFile) {
    this.hasHarassmentFile = hasHarassmentFile;
  }
}
