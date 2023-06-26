package es.udc.pcv.backend.model.exceptions;

public class InvalidStatusTransitionException extends Exception {

  private String currentStatus;
  private String newStatus;
  private String requiredFileName;

  public InvalidStatusTransitionException(String currentStatus, String newStatus) {

    this.currentStatus = currentStatus;
    this.newStatus = newStatus;
    this.requiredFileName = null;

  }
  public InvalidStatusTransitionException(String currentStatus, String newStatus, String requiredFileName) {

    this.currentStatus = currentStatus;
    this.newStatus = newStatus;
    this.requiredFileName = requiredFileName;

  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public String getNewStatus() {
    return newStatus;
  }

  public String getRequiredFileName() {
    return requiredFileName;
  }
}
