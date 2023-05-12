package es.udc.pcv.backend.model.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "\"Participation\"")
public class Participation {
  public enum ParticipationState {
    PENDING("pending"),
    SCHEDULED("scheduled"),
    REJECTED("rejected"),
    APPROVED("approved"),
    ACCEPTED("accepted"),
    DELETED("deleted");

    private final String value;

    ParticipationState(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  private Long id;
  private int totalHours;
  private ParticipationState state;
  private boolean isRecommended;
  private LocalDate registerDate;
  private Project project;
  private Volunteer volunteer;

  public Participation() {
  }

  public Participation(int totalHours,
                       ParticipationState state, boolean isRecommended,
                       LocalDate registerDate, Project project, Volunteer volunteer) {
    this.totalHours = totalHours;
    this.state = state;
    this.isRecommended = isRecommended;
    this.registerDate = registerDate;
    this.project = project;
    this.volunteer = volunteer;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "\"totalHours\"")
  public int getTotalHours() {
    return totalHours;
  }

  public void setTotalHours(int totalHours) {
    this.totalHours = totalHours;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "\"state\"")
  public ParticipationState getState() {
    return state;
  }

  public void setState(ParticipationState state) {
    this.state = state;
  }

  @Column(name = "\"isRecommended\"")
  public boolean isRecommended() {
    return isRecommended;
  }

  public void setRecommended(boolean recommended) {
    isRecommended = recommended;
  }

  @Column(name = "\"registerDate\"")
  public LocalDate getRegisterDate() {
    return registerDate;
  }

  public void setRegisterDate(LocalDate registerDate) {
    this.registerDate = registerDate;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"projectId\"")
  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "\"volunteerRecordId\"")
  public Volunteer getVolunteer() {
    return volunteer;
  }

  public void setVolunteer(Volunteer volunteer) {
    this.volunteer = volunteer;
  }
}
