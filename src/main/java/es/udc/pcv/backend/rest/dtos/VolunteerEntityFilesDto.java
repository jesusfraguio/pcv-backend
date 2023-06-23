package es.udc.pcv.backend.rest.dtos;

public class VolunteerEntityFilesDto {
  private boolean hasCertFile;
  private boolean hasHarassmentFile;
  private boolean hasDniFile;

  public VolunteerEntityFilesDto(boolean hasCertFile, boolean hasHarassmentFile, boolean hasDniFile) {
    this.hasCertFile = hasCertFile;
    this.hasHarassmentFile = hasHarassmentFile;
    this.hasDniFile = hasDniFile;
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

  public boolean isHasDniFile() {
    return hasDniFile;
  }

  public void setHasDniFile(boolean hasDniFile) {
    this.hasDniFile = hasDniFile;
  }
}
