package es.udc.pcv.backend.model.entities;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "\"File\"")
public class File {
  public enum FileType {
    DNI("dni"),
    HARASSMENT_CERT("harassementCert"),
    PHOTO("photo"),
    FORMATION_COMPLETED("formationCompleted"),
    AGREEMENT_FILE_SIGNED_BY_ENTITY("agreementFileSignedByEntity"),
    AGREEMENT_FILE_SIGNED_BY_BOTH("agreementFileSignedByBoth"),
    AGREEMENT_FILE("agreementFile"),
    LOGO("logo");

    private final String value;

    FileType(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  private UUID id;
  private Date date;
  private String originalName;
  private FileType fileType;

  public File() {
  }

  public File(UUID id, Date date, String originalName,
              FileType fileType) {
    this.id = id;
    this.date = date;
    this.originalName = originalName;
    this.fileType = fileType;
  }


  @Id
  @Column(name = "\"id\"", unique = true, nullable = false)
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  @Column(name = "\"date\"")
  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  @Column(name = "\"originalName\"")
  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "\"filetype\"")
  public FileType getFileType() {
    return fileType;
  }

  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }
}
