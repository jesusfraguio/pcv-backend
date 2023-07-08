package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.FileDao;
import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.ParticipationDao;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.ProjectDao;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.entities.VolunteerDao;
import es.udc.pcv.backend.model.exceptions.AlreadyParticipatingException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.InvalidStatusTransitionException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.exceptions.ProjectIsPausedException;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
import es.udc.pcv.backend.rest.dtos.ParticipationStatusDto;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
  @Autowired
  private FileDao fileDao;

  @Override
  public Participation createMyParticipation(ParticipationDto participationData, Long userId) throws
      InstanceNotFoundException, AlreadyParticipatingException, PermissionException,
      ProjectIsPausedException{
    Optional<Volunteer> volunteer = volunteerDao.findByUserId(participationData.getVolunteerId());
    if(!volunteer.isPresent()){
      throw new InstanceNotFoundException("project.entities.volunteer",participationData.getVolunteerId());
    }
    //basic user who is not representative trying to bypass system
    if(volunteer.get().getUser().getId().longValue() != userId.longValue()){
      throw new PermissionException();
    }
    Optional<Project> project = projectDao.findById(participationData.getProjectId());
    if(!project.isPresent()){
      throw new InstanceNotFoundException("project.entities.project",participationData.getProjectId());
    }
    if(project.get().isPaused() || !project.get().isVisible()){
      throw new ProjectIsPausedException();
    }
    if(participationDao.findByProjectIdAndVolunteerId(project.get().getId(),volunteer.get().getId()).isPresent()){
      throw new AlreadyParticipatingException();
    }
    return participationDao.save(new Participation(0, Participation.ParticipationState.PENDING,
        participationData.isRecommended(), LocalDate.now(),project.get(),volunteer.get()));
  }

  @Override
  public Participation createParticipation(ParticipationDto participationData, Long representativeId)
      throws InstanceNotFoundException, AlreadyParticipatingException, PermissionException {
    Optional<Volunteer> volunteer = volunteerDao.findById(participationData.getVolunteerId());
    if(!volunteer.isPresent()){
      throw new InstanceNotFoundException("project.entities.volunteer",participationData.getVolunteerId());
    }
    Optional<Representative> representative = representativeDao.findById(representativeId);
    // no need check isPresent since this endpoint is only for representatives
    if(!((fileDao.existsByVolunteerAndFileTypeAndEntidad(volunteer.get(), File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH,representative.get().getEntity())
    ) || (participationDao.existsByProjectEntityIdAndVolunteerId(representative.get().getEntity().getId(),volunteer.get().getId())))){
      throw new PermissionException();
    }
    Optional<Project> project = projectDao.findById(participationData.getProjectId());
    if(!project.isPresent()){
      throw new InstanceNotFoundException("project.entities.project",participationData.getProjectId());
    }
    if(participationDao.findByProjectIdAndVolunteerId(project.get().getId(),volunteer.get().getId()).isPresent()){
      throw new AlreadyParticipatingException();
    }
    return participationDao.save(new Participation(0, Participation.ParticipationState.SCHEDULED,
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
        if (!(fileDao.findByEntidadAndVolunteerAndFileType(representative.get().getEntity(), participation.get()
            .getVolunteer(), File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH).isPresent())) {
          throw new InvalidStatusTransitionException(currentStatus.getValue(), updatedStatus.getValue());
        }
        // there are children -> law requirement requires volunteer harassment certificate
        if (updatedStatus == Participation.ParticipationState.ACCEPTED && participation.get().getProject().isAreChildren()
            && !(fileDao.findByVolunteerAndFileType(participation.get().getVolunteer(), File.FileType.HARASSMENT_CERT).isPresent())){
          throw new InvalidStatusTransitionException(currentStatus.getValue(), updatedStatus.getValue(), File.FileType.HARASSMENT_CERT.toString());
        }
        //scanned dni is required to accept a participation
        if(updatedStatus == Participation.ParticipationState.ACCEPTED
            && !(fileDao.findByVolunteerAndFileType(participation.get().getVolunteer(), File.FileType.DNI).isPresent())){
          throw new InvalidStatusTransitionException(currentStatus.getValue(), updatedStatus.getValue(), File.FileType.DNI.toString());
        }
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

  @Override
  public File updateMyParticipationCertFile(Long userId, Long id,
                                             MultipartFile multipartFile)
      throws InstanceNotFoundException, PermissionException, IOException,
      InvalidStatusTransitionException {
    Optional<Participation> participationOpt = participationDao.findById(id);
    if(!participationOpt.isPresent()){
      throw new InstanceNotFoundException("project.entities.participation",id);
    }
    User user1 = participationOpt.get().getVolunteer().getUser();
    if(!(user1 != null && userId == user1.getId())){
      throw new PermissionException();
    }
    Participation participation = participationOpt.get();
    //there must be a harassment cert
    if(participation.getProject().isAreChildren() &&
        !(fileDao.findByVolunteerAndFileType(participation.getVolunteer(), File.FileType.HARASSMENT_CERT).isPresent())){
      throw new InvalidStatusTransitionException(Participation.ParticipationState.APPROVED.getValue(),
          Participation.ParticipationState.ACCEPTED.getValue(), File.FileType.HARASSMENT_CERT.toString());
    }
    //there must be a scanned dni
    if(participation.getState() == Participation.ParticipationState.APPROVED &&
        !(fileDao.findByVolunteerAndFileType(participation.getVolunteer(), File.FileType.DNI).isPresent())){
      throw new InvalidStatusTransitionException(Participation.ParticipationState.APPROVED.getValue(),
          Participation.ParticipationState.ACCEPTED.getValue(), File.FileType.DNI.toString());
    }
    String uploadDir = "./participations/certFiles/";
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    InputStream inputStream = multipartFile.getInputStream();
    Tika tika = new Tika();
    String mimeType = tika.detect(inputStream);
    String extension = mimeType.split("/")[1];
    UUID randomUIID = UUID.randomUUID();
    String fileName = randomUIID.toString();
    Path filePath = Paths.get(uploadDir + fileName + "." + extension);
    if (Files.exists(filePath)) {
      throw new IOException("File already exists: " + filePath);
    }
    Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    Optional<File> oldFile = fileDao.findByEntidadAndVolunteerAndFileType(participation.getProject().getEntity(), participation.getVolunteer(), File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH);
    if (oldFile.isPresent()) {
      File newFile = oldFile.get();
      Path path = Paths.get("./participations/certFiles/" + newFile.getId().toString() + "." + newFile.getExtension());
      fileDao.delete(newFile);
      Files.delete(path);
    }
    File saved = fileDao.save(new File(randomUIID,new Date(),multipartFile.getOriginalFilename(),File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH,
        extension,participationOpt.get().getProject().getEntity(),participationOpt.get().getVolunteer()));
    if(participation.getState()== Participation.ParticipationState.APPROVED){
      participation.setState(Participation.ParticipationState.ACCEPTED);
    }
    return saved;
  }

}
