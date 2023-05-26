package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.ParticipationDao;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.ProjectDao;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.entities.VolunteerDao;
import es.udc.pcv.backend.model.exceptions.AlreadyParticipatingException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.InvalidStatusTransitionException;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
import es.udc.pcv.backend.rest.dtos.ParticipationStatusDto;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VolunteerServiceImpl implements VolunteerService {

  @Autowired
  private ParticipationDao participationDao;
  @Autowired
  private VolunteerDao volunteerDao;
  @Autowired
  private ProjectDao projectDao;
  @Autowired
  private RepresentativeDao representativeDao;

  @Override
  public Participation createParticipation(ParticipationDto participationData) throws
      InstanceNotFoundException, AlreadyParticipatingException {
    Optional<Volunteer> volunteer = volunteerDao.findByUserId(participationData.getVolunteerId());
    if(!volunteer.isPresent()){
      throw new InstanceNotFoundException("project.entities.volunteer",participationData.getVolunteerId());
    }
    Optional<Project> project = projectDao.findById(participationData.getProjectId());
    if(!project.isPresent()){
      throw new InstanceNotFoundException("project.entities.project",participationData.getProjectId());
    }
    if(participationDao.findByProjectIdAndVolunteerId(project.get().getId(),volunteer.get().getId()).isPresent()){
      throw new AlreadyParticipatingException();
    }
    return participationDao.save(new Participation(0, Participation.ParticipationState.PENDING,
        participationData.isRecommended(), LocalDate.now(),project.get(),volunteer.get()));
  }

  @Override
  public Block<Participation> findAllMyParticipations(Long userId, PageableDto pageableDto) throws InstanceNotFoundException {
    Optional<Volunteer> volunteer = volunteerDao.findByUserId(userId);
    if(!volunteer.isPresent()){
      throw new InstanceNotFoundException("project.entities.volunteer",userId);
    }
    Page<Participation> participationPage = participationDao.findAllByVolunteerId(volunteer.get().getId(),
        PageRequest.of(pageableDto.getPage(), pageableDto.getSize()));
    return new Block<>(participationPage.getContent(),participationPage.hasNext());

  }

  @Override
  public Participation updateParticipation(Long representativeId, Long id,
                                           ParticipationStatusDto statusDto)
      throws InstanceNotFoundException, InvalidStatusTransitionException {
    Optional<Participation> participation = participationDao.findById(id);

    if(!participation.isPresent()){
      throw new InstanceNotFoundException("project.entities.participation",id);
    }
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if((!representative.isPresent()) ||
        (representative.get().getEntity().getId() != participation.get().getProject().getEntity().getId())){
      throw new InstanceNotFoundException("project.entities.participation",id);
    }

    // Check if the new state is compatible with current participation state
    Participation.ParticipationState updatedStatus;
    try {
      updatedStatus = Participation.ParticipationState.valueOf(statusDto.getStatus());
    } catch (IllegalArgumentException e) {
      throw new InstanceNotFoundException("project.entities.participation",id);
    }

    Participation.ParticipationState currentStatus = participation.get().getState();

    switch (currentStatus) {
      case PENDING:
        if (updatedStatus != Participation.ParticipationState.SCHEDULED && updatedStatus != Participation.ParticipationState.REJECTED) {
          throw new InvalidStatusTransitionException(currentStatus.getValue(), updatedStatus.getValue());
        }
        break;
      case SCHEDULED:
        if (updatedStatus != Participation.ParticipationState.APPROVED && updatedStatus != Participation.ParticipationState.REJECTED) {
          throw new InvalidStatusTransitionException(currentStatus.getValue(), updatedStatus.getValue());
        }
        break;
      case APPROVED:
        if (updatedStatus != Participation.ParticipationState.ACCEPTED && updatedStatus != Participation.ParticipationState.DELETED) {
          throw new InvalidStatusTransitionException(currentStatus.getValue(), updatedStatus.getValue());
        }
        break;
      case ACCEPTED:
        if (updatedStatus != Participation.ParticipationState.DELETED) {
          throw new InvalidStatusTransitionException(currentStatus.getValue(), updatedStatus.getValue());
        }
        break;
      case REJECTED:
      case DELETED:
        throw new InvalidStatusTransitionException(currentStatus.getValue(), updatedStatus.getValue());
      default:
        throw new InvalidStatusTransitionException(currentStatus.getValue(), null);
    }
    participation.get().setState(updatedStatus);
    return participation.get();

  }

}
