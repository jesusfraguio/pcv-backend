package es.udc.pcv.backend.model.to;

import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.entities.Volunteer;

public class UserWithVolunteer {
  private User user;
  private Volunteer volunteer;

  public UserWithVolunteer(User user, Volunteer volunteer) {
    this.user = user;
    this.volunteer = volunteer;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Volunteer getVolunteer() {
    return volunteer;
  }

  public void setVolunteer(Volunteer volunteer) {
    this.volunteer = volunteer;
  }
}
