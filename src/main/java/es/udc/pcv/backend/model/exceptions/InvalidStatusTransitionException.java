package es.udc.pcv.backend.model.exceptions;

public class InvalidStatusTransitionException extends Exception {

  private String currentStatus;
  private String newStatus;

  public InvalidStatusTransitionException(String currentStatus, String newStatus) {

    this.currentStatus = currentStatus;
    this.newStatus = newStatus;

  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public String getNewStatus() {
    return newStatus;
  }
}
