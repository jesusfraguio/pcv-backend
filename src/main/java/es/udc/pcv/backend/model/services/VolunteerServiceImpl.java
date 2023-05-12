package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.ParticipationDao;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.ProjectDao;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.entities.VolunteerDao;
import es.udc.pcv.backend.model.exceptions.AlreadyParticipatingException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
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

}
