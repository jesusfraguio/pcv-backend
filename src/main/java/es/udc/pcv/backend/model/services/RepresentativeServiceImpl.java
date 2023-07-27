package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.CollaborationAreaDao;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.EntidadDao;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.FileDao;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.OdsDao;
import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.ParticipationDao;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.ProjectDao;
import es.udc.pcv.backend.model.entities.ProjectSpecifications;
import es.udc.pcv.backend.model.entities.RegisteredHours;
import es.udc.pcv.backend.model.entities.RegisteredHoursDao;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.entities.Task;
import es.udc.pcv.backend.model.entities.TaskDao;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.entities.VolunteerDao;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.ParticipationIsInDateException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.to.ResourceWithType;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.ProjectFiltersDto;
import es.udc.pcv.backend.rest.dtos.RegisteredHoursDto;
import es.udc.pcv.backend.rest.dtos.SelectorDataDto;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class RepresentativeServiceImpl implements RepresentativeService {
  @Autowired
  private ProjectDao projectDao;

  @Autowired
  private TaskDao taskDao;

  @Autowired
  private CollaborationAreaDao collaborationAreaDao;

  @Autowired
  private EntidadDao entidadDao;

  @Autowired
  private OdsDao odsDao;

  @Autowired
  private RepresentativeDao representativeDao;

  @Autowired
  private ParticipationDao participationDao;

  @Autowired
  private FileDao fileDao;

  @Autowired
  private VolunteerDao volunteerDao;

  @Autowired
  private RegisteredHoursDao registeredHoursDao;

  @Value("${file.base-path}")
  private String basePath;

  @Override
  public Project createProject(ProjectDto projectDto, long userId)
      throws InstanceNotFoundException {
    Optional<Representative> user = representativeDao.findById(userId);
    if (!user.isPresent() || user.get().getEntity().getId() != projectDto.getEntityId()) {
      throw new InstanceNotFoundException("project.entities.entity", projectDto.getEntityId());
    }
    Optional<CollaborationArea> collaborationArea =
        collaborationAreaDao.findById(projectDto.getAreaId());
    if (!collaborationArea.isPresent()) {
      throw new InstanceNotFoundException("project.entities.collaborationArea",
          projectDto.getAreaId());
    }

    Set<Ods> odsList = projectDto.getOds().stream()
        .map(odsId -> odsDao.findById(odsId))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
    if (odsList.isEmpty()) {
      throw new InstanceNotFoundException("project.entities.ods", projectDto.getOds().get(0));
    }

    Project project = new Project(projectDto.getName(), projectDto.getShortDescription(),
        projectDto.getLongDescription(),
        projectDto.getLocality(), projectDto.getSchedule(), projectDto.getCapacity(),
        projectDto.getPreferableVolunteer(),
        projectDto.isAreChildren(), projectDto.isVisible(), projectDto.isPaused(),
        entidadDao.findById(projectDto.getEntityId()).get(), collaborationArea.get());
    project.setOds(odsList);
    if (projectDto.getId() != null) {
      //for updating
      project.setId(projectDto.getId());
    }
    project = projectDao.save(project);
    if (projectDto.getId() != null) {
      project.setTasks(null);
      taskDao.deleteAllByProjectId(projectDto.getId());
    }
    List<Task> taskList = new ArrayList<>();
    for (String name : projectDto.getTasks()) {
      taskList.add(taskDao.save(new Task(name, project)));
    }
    project.setTasks(taskList);
    projectDao.save(project);
    return project;
  }

  @Override
  public Entidad getMyEntity(long userId) {
    return representativeDao.findById(userId).get().getEntity();
  }

  @Override
  public List<Ods> getAllOds() {
    Iterable<Ods> odss = odsDao.findAll(Sort.by(Sort.Direction.ASC, "name"));
    List<Ods> odsAsList = new ArrayList<>();

    odss.forEach(odsAsList::add);

    return odsAsList;
  }

  @Override
  public List<CollaborationArea> getAllCollaborationArea() {
    Iterable<CollaborationArea> areas =
        collaborationAreaDao.findAll(Sort.by(Sort.Direction.ASC, "name"));
    List<CollaborationArea> areasAsList = new ArrayList<>();

    areas.forEach(areasAsList::add);

    return areasAsList;
  }

  @Override
  public Block<Project> findProjectsBy(ProjectFiltersDto projectFiltersDto,
                                       PageableDto pageableDto) {
    String[] allowedSortColumns = {"name", "locality", "collaborationAreaId"};
    Pageable pageable = pageableDtoToPageable(pageableDto, allowedSortColumns);
    Page<Project> projectsPage = projectDao.findAll(
        ProjectSpecifications.searchProjects(projectFiltersDto.getName(),
            projectFiltersDto.getLocality(), projectFiltersDto.getCollaborationAreaId()), pageable);

    return new Block<>(projectsPage.getContent(), projectsPage.hasNext());
  }

  @Override
  public ResourceWithType getLogo(Long entityId) throws InstanceNotFoundException {
    Optional<Entidad> entity = entidadDao.findById(entityId);
    if (!entity.isPresent()) {
      throw new InstanceNotFoundException("project.entities.entity", entityId);
    }
    File file = entity.get().getLogo();
    if (file == null) {
      throw new InstanceNotFoundException("project.entities.fileLogo", entityId);
    }
    java.io.File savedFile =
        new java.io.File("./entities/logos/" + file.getId() + "." + file.getExtension());

    Resource resource;
    try {
      resource = new UrlResource(savedFile.toURI());
    } catch (MalformedURLException e) {
      throw new InstanceNotFoundException("project.entities.entity", entityId);
    }
    return new ResourceWithType(resource, file.getExtension());
  }

  @Override
  public ResourceWithType getAgreementFile(Long entityId) throws InstanceNotFoundException {
    Optional<Entidad> entity = entidadDao.findById(entityId);
    if (!entity.isPresent()) {
      throw new InstanceNotFoundException("project.entities.entity", entityId);
    }
    File file = entity.get().getCertFile();
    if (file == null) {
      throw new InstanceNotFoundException("project.entities.fileCert", entityId);
    }
    java.io.File savedFile =
        new java.io.File("./entities/certs/" + file.getId() + "." + file.getExtension());

    Resource resource;
    try {
      resource = new UrlResource(savedFile.toURI());
    } catch (MalformedURLException e) {
      throw new InstanceNotFoundException("project.entities.entity", entityId);
    }
    return new ResourceWithType(resource, file.getExtension());
  }

  @Override
  public ResourceWithType getVolunteerFile(Long representativeId, Long volunteerId, String fileType)
      throws InstanceNotFoundException {
    File.FileType requestedFile = File.FileType.valueOf(fileType);
    Optional<Volunteer> volunteer = volunteerDao.findById(volunteerId);
    if (!volunteer.isPresent()) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
    }
    Optional<Representative> representative = representativeDao.findById(representativeId);
    //if there is no permission volunteer does not exist
    if (!(fileDao.findByEntidadAndVolunteerAndFileType(representative.get().getEntity(),
        volunteer.get(),
        File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH).isPresent())) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
    }
    Optional<File> file;
    if (requestedFile.equals(File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH)) {
      file = fileDao.findByEntidadAndVolunteerAndFileType(representative.get().getEntity(),
          volunteer.get(), requestedFile);
    } else {
      file = fileDao.findByVolunteerAndFileType(volunteer.get(), requestedFile);
    }
    if (!file.isPresent()) {
      throw new InstanceNotFoundException("project.entities.file", volunteerId);
    }

    Resource resource;
    java.io.File savedFile = getSystemFile(file.get(), requestedFile);
    try {
      resource = new UrlResource(savedFile.toURI());
    } catch (MalformedURLException e) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
    }
    return new ResourceWithType(resource, file.get().getExtension());

  }

  @Override
  public Project getProject(long projectId) throws InstanceNotFoundException {
    Optional<Project> project = projectDao.findById(projectId);
    if (!project.isPresent()) {
      throw new InstanceNotFoundException("project.entities.project", projectId);
    } else {
      return project.get();
    }
  }

  @Override
  public Block<Project> getMyEntityProjects(Long representativeId, Long entityId, PageableDto pageableDto)
      throws InstanceNotFoundException {
    //Currently, using this method to get any entity project's since project detail is public and yet only ProjectSummary is exposed
    Long searchEntityId = entityId;
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    if(entityId!=null){
      Optional<Entidad> entity = entidadDao.findById(entityId);
      if(!entity.isPresent()){
        searchEntityId = representative.get().getEntity().getId();
      }
    }
    else{
      searchEntityId = representative.get().getEntity().getId();
    }
    Page<Project> projectPage =
        projectDao.findAllByEntityId(searchEntityId,
            PageRequest.of(pageableDto.getPage(), pageableDto.getSize()));
    return new Block<>(projectPage.getContent(), projectPage.hasNext());
  }

  @Override
  public Block<Participation> findAllPendingParticipation(Long representativeId,
                                                          PageableDto pageableDto)
      throws InstanceNotFoundException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    Page<Participation> participationPage = participationDao.findAllByProjectEntityIdAndState(
        representative.get().getEntity().getId(),
        Participation.ParticipationState.PENDING,
        PageRequest.of(pageableDto.getPage(), pageableDto.getSize()));

    return new Block<>(participationPage.getContent(), participationPage.hasNext());
  }

  @Override
  public Block<Participation> findAllProjectParticipation(Long representativeId, Long projectId,
                                                          PageableDto pageableDto)
      throws InstanceNotFoundException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    Project project = getProject(projectId);
    if (project.getEntity().getId() != representative.get().getEntity().getId()) {
      // Administrador no podra ver participaciones de otras entidades
      throw new InstanceNotFoundException("project.entities.project", projectId);
    }

    String[] allowedSortColumns = {"state", "volunteerSurname", "volunteerName", "totalHours"};

    Pageable pageable = pageableDtoToPageable(pageableDto, allowedSortColumns);
    Page<Participation> participationPage = participationDao.findAllByProjectId(
        projectId, pageable);

    return new Block<>(participationPage.getContent(), participationPage.hasNext());
  }

  @Override
  public File updateVolunteerCertFile(Long userId, Long id, MultipartFile multipartFile)
      throws InstanceNotFoundException, PermissionException, IOException {
    Optional<Participation> participationOpt = participationDao.findById(id);
    if (!participationOpt.isPresent()) {
      throw new InstanceNotFoundException("project.entities.participation", id);
    }
    Optional<Representative> representative = representativeDao.findById(userId);
    if (!(representative.isPresent() && representative.get().getEntity().getId() ==
        participationOpt.get().getProject().getEntity().getId())) {
      throw new PermissionException();
    }
    String uploadDir = basePath+"participations/certFiles/";
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
    Optional<File> oldFile = fileDao.findByEntidadAndVolunteerAndFileType(
        participationOpt.get().getProject().getEntity(),
        participationOpt.get().getVolunteer(), File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH);
    if (oldFile.isPresent()) {
      File newFile = oldFile.get();
      Path path = Paths.get("./participations/certFiles/" + newFile.getId().toString() + "." +
          newFile.getExtension());
      fileDao.delete(newFile);
      try {
        Files.delete(path);
      }catch (IOException e){
        //If there is no old file in disk because it was already deleted (low chances) app will keep going right
      }
    }
    File saved = fileDao.save(new File(randomUIID, new Date(), multipartFile.getOriginalFilename(),
        File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH,
        extension, representative.get().getEntity(), participationOpt.get().getVolunteer()));
    Participation participation = participationOpt.get();
    if (participation.getState() == Participation.ParticipationState.APPROVED) {
      participation.setState(Participation.ParticipationState.ACCEPTED);
    }
    return saved;
  }

  @Override
  public File updateVolunteerHarassmentCert(Long representativeId, Long volunteerId,
                                            MultipartFile multipartFile)
      throws InstanceNotFoundException, IOException {
    String uploadDir = basePath+"users/harassmentCert/";
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        throw new IOException("Could not create path: " + dir.getPath());
      }
    }
    Optional<Volunteer> volunteer = volunteerDao.findById(volunteerId);
    if (!volunteer.isPresent()) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
    }
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    if (!(fileDao.findByEntidadAndVolunteerAndFileType(representative.get().getEntity(),
        volunteer.get(),
        File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH).isPresent())) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
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
    Optional<File> oldFile =
        fileDao.findByVolunteerAndFileType(volunteer.get(), File.FileType.HARASSMENT_CERT);
    if (oldFile.isPresent()) {
      File newFile = oldFile.get();
      Path path = Paths.get(
          "./users/harassmentCert/" + newFile.getId().toString() + "." + newFile.getExtension());
      fileDao.delete(newFile);
      try {
        Files.delete(path);
      }catch (IOException e){
        //If there is no old file in disk because it was already deleted (low chances) app will keep going right
      }
    }
    return fileDao.save(new File(randomUIID, new Date(), multipartFile.getOriginalFilename(),
        File.FileType.HARASSMENT_CERT,
        extension, null, volunteer.get()));
  }

  @Override
  public File updateVolunteerDNI(Long representativeId, Long volunteerId,
                                 MultipartFile dni) throws InstanceNotFoundException, IOException {
    String uploadDir = basePath+"users/dni/";
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    Optional<Volunteer> volunteer = volunteerDao.findById(volunteerId);
    if (!volunteer.isPresent()) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
    }
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    //if there is no permission volunteer does not exist
    if (!(fileDao.findByEntidadAndVolunteerAndFileType(representative.get().getEntity(),
        volunteer.get(),
        File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH).isPresent())) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
    }
    InputStream inputStream = dni.getInputStream();
    Tika tika = new Tika();
    String mimeType = tika.detect(inputStream);
    String extension = mimeType.split("/")[1];
    UUID randomUIID = UUID.randomUUID();
    String fileName = randomUIID.toString();
    Path filePath = Paths.get(uploadDir + fileName + "." + extension);
    if (Files.exists(filePath)) {
      throw new IOException("File already exists: " + filePath);
    }
    Files.copy(dni.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    Optional<File> oldFile = fileDao.findByVolunteerAndFileType(volunteer.get(), File.FileType.DNI);
    if (oldFile.isPresent()) {
      File newFile = oldFile.get();
      Path path =
          Paths.get("./users/dni/" + newFile.getId().toString() + "." + newFile.getExtension());
      fileDao.delete(newFile);
      try {
        Files.delete(path);
      }catch (IOException e){
        //If there is no old file in disk because it was already deleted (low chances) app will keep going right
      }
    }
    return fileDao.save(
        new File(randomUIID, new Date(), dni.getOriginalFilename(), File.FileType.DNI,
            extension, null, volunteer.get()));
  }

  @Override
  public File uploadVolunteerPhoto(Long representativeId, Long volunteerId, MultipartFile photo)
      throws InstanceNotFoundException, IOException {
    String uploadDir = basePath+"users/photo/";
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    Optional<Volunteer> volunteer = volunteerDao.findById(volunteerId);
    if (!volunteer.isPresent()) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
    }
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    //if there is no permission volunteer does not exist
    if (!(fileDao.findByEntidadAndVolunteerAndFileType(representative.get().getEntity(),
        volunteer.get(),
        File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH).isPresent())) {
      throw new InstanceNotFoundException("project.entities.volunteer", volunteerId);
    }
    InputStream inputStream = photo.getInputStream();
    Tika tika = new Tika();
    String mimeType = tika.detect(inputStream);
    String extension = mimeType.split("/")[1];
    UUID randomUIID = UUID.randomUUID();
    String fileName = randomUIID.toString();
    Path filePath = Paths.get(uploadDir + fileName + "." + extension);
    if (Files.exists(filePath)) {
      throw new IOException("File already exists: " + filePath);
    }
    Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    Optional<File> oldFile =
        fileDao.findByVolunteerAndFileType(volunteer.get(), File.FileType.PHOTO);
    if (oldFile.isPresent()) {
      File newFile = oldFile.get();
      Path path =
          Paths.get("./users/photo/" + newFile.getId().toString() + "." + newFile.getExtension());
      fileDao.delete(newFile);
      try {
        Files.delete(path);
      }catch (IOException e){
        //If there is no old file in disk because it was already deleted (low chances) app will keep going right
      }
    }
    return fileDao.save(
        new File(randomUIID, new Date(), photo.getOriginalFilename(), File.FileType.PHOTO,
            extension, null, volunteer.get()));
  }

  @Override
  public Block<Volunteer> findMyEntityVolunteers(Long representativeId, PageableDto pageableDto)
      throws InstanceNotFoundException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }

    String[] allowedSortColumns =
        {"volunteerSurname", "volunteerName", "volunteerPhone", "volunteerDni"};

    Pageable pageable = pageableDtoToPageable(pageableDto, allowedSortColumns);

    Entidad entity = representative.get().getEntity();
    // There won't be shown volunteers who registered themselves that did not sign (or were assigned in the project) agreement file yet
    Page<File> filePage = fileDao.findAllByEntidadAndFileType(
        entity, File.FileType.AGREEMENT_FILE_SIGNED_BY_BOTH, pageable);

    List<Volunteer> volunteers = new ArrayList<>();
    for (File fileEntity : filePage.getContent()) {
      Volunteer volunteer = fileEntity.getVolunteer();
      if (volunteer != null && !volunteers.contains(volunteer)) {
        volunteers.add(volunteer);
      }
    }
    return new Block<>(volunteers, filePage.hasNext());

  }

  @Override
  public List<RegisteredHours> findAllHoursWithinDates(Long representativeId, Long projectId,
                                                       LocalDate startDate,
                                                       LocalDate endDate)
      throws InstanceNotFoundException, PermissionException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    if (projectId != null) {
      Optional<Project> project = projectDao.findById(projectId);
      if (!project.isPresent()) {
        throw new InstanceNotFoundException("project.entities.project", projectId);
      }
      if (project.get().getEntity() != representative.get().getEntity()) {
        throw new PermissionException();
      }
      return registeredHoursDao.findByParticipationProjectAndDateBetween(project.get(), startDate,
          endDate);
    } else {
      return registeredHoursDao.findByParticipationProjectEntityAndDateBetween(representative.get()
          .getEntity(), startDate, endDate);
    }
  }

  @Override
  public List<SelectorDataDto> findAllMyEntityProjects(Long representativeId)
      throws InstanceNotFoundException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    return projectDao.findAllByEntityId(representative.get().getEntity().getId(),
        PageRequest.of(0, 6000)).getContent().stream().map(
        project -> new SelectorDataDto(project.getId(), project.getName())).collect(
        Collectors.toList());
  }

  @Override
  public List<SelectorDataDto> findAllProjectParticipations(Long representativeId, Long projectId)
      throws InstanceNotFoundException, PermissionException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    Optional<Project> project = projectDao.findById(projectId);
    if (!project.isPresent()) {
      throw new InstanceNotFoundException("project.entities.project", projectId);
    }
    if (project.get().getEntity() != representative.get().getEntity()) {
      throw new PermissionException();
    }
    return participationDao.findAllByProjectIdAndState(projectId,
            Participation.ParticipationState.ACCEPTED, PageRequest.of(0, 6000)).getContent().stream()
        .map(participation ->
            new SelectorDataDto(participation.getId(),
                (participation.getVolunteer().getName() + " " +
                    participation.getVolunteer().getSurname()))).collect(
            Collectors.toList());
  }

  @Override
  public RegisteredHours createHourRegister(Long representativeId,
                                            RegisteredHoursDto registeredHoursDto)
      throws InstanceNotFoundException, ParticipationIsInDateException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    Optional<Participation> participation = participationDao.findById(
        registeredHoursDto.getParticipationId());
    if (!participation.isPresent() || (participation.get().getProject().getEntity() != representative.get().getEntity())) {
      throw new InstanceNotFoundException("project.entities.participation",
          registeredHoursDto.getParticipationId());
    }
    Optional<RegisteredHours> savedRegisteredHours =
        registeredHoursDao.findByDateAndParticipation(registeredHoursDto.getDate(),
            participation.get());

    if (!savedRegisteredHours.isPresent()) {
      RegisteredHours registeredHoursResult = registeredHoursDao.save(
          new RegisteredHours(registeredHoursDto.getHours(), registeredHoursDto.getDate(),
              participation.get()));
      //update participation total hours for performance if only total hours are queried later
      try {
        participation.get()
            .setTotalHours(participation.get().getTotalHours() + registeredHoursResult.getHours());
      }
      catch (OptimisticLockingFailureException ex) {
        registeredHoursDao.delete(registeredHoursResult);
        throw new InstanceNotFoundException("project.entities.participation",participation.get().getId());
      }
      return registeredHoursResult;
    }
    else {
      //no allowing saving the same participation again in the same date
      throw new ParticipationIsInDateException();
    }

  }

  @Override
  public boolean deleteHourRegister(Long representativeId, Long hourRegisterId)
      throws InstanceNotFoundException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if (!representative.isPresent()) {
      throw new InstanceNotFoundException("project.entities.representative", representativeId);
    }
    Optional<RegisteredHours> hours = registeredHoursDao.findById(
        hourRegisterId);
    if(!hours.isPresent()){
      throw new InstanceNotFoundException("project.entities.registeredHours", hourRegisterId);
    }
    try {
      hours.get().getParticipation()
          .setTotalHours(hours.get().getParticipation().getTotalHours() - hours.get().getHours());
    }
    catch (OptimisticLockingFailureException ex) {
      throw new InstanceNotFoundException("project.entities.registeredHours",hourRegisterId);
    }
    registeredHoursDao.delete(hours.get());
    return true;
  }

  private Pageable pageableDtoToPageable(PageableDto pageableDto, String[] allowedSortColumns) {
    return getPageable(pageableDto, allowedSortColumns);
  }

  protected static Pageable getPageable(PageableDto pageableDto, String[] allowedSortColumns) {
    boolean isSorted = pageableDto.getSortValue() != null;
    if (pageableDto.getSortValue() != null &&
        !Arrays.asList(allowedSortColumns).contains(pageableDto.getSortValue())) {
      throw new IllegalArgumentException("Invalid sort value");
    }
    Pageable pageable;
    if (isSorted) {
      if (pageableDto.getSortOrder().equals("desc")) {
        pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(),
            Sort.by(Sort.Direction.DESC, pageableDto.getSortValue()));
      } else {
        pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(),
            Sort.by(Sort.Direction.ASC, pageableDto.getSortValue()));
      }
    } else {
      pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
    }
    return pageable;
  }

  private java.io.File getSystemFile(File file, File.FileType fileType) {

    switch (fileType) {
      case DNI:
        return new java.io.File("./users/dni/" + file.getId() + "." + file.getExtension());
      case HARASSMENT_CERT:
        return new java.io.File(
            "./users/harassmentCert/" + file.getId() + "." + file.getExtension());
      case PHOTO:
        return new java.io.File("./users/photo/" + file.getId() + "." + file.getExtension());
      case AGREEMENT_FILE:
        return new java.io.File("./entities/certs/" + file.getId() + "." + file.getExtension());
      case AGREEMENT_FILE_SIGNED_BY_ENTITY:
      case AGREEMENT_FILE_SIGNED_BY_BOTH:
        return new java.io.File(
            "./participations/certFiles/" + file.getId() + "." + file.getExtension());
      default:
        return null;
    }
  }
}
