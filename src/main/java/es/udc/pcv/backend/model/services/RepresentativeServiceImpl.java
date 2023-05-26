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
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.entities.Task;
import es.udc.pcv.backend.model.entities.TaskDao;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.to.ResourceWithType;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.ProjectFiltersDto;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RepresentativeServiceImpl implements RepresentativeService{
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

  @Override
  public Project createProject(ProjectDto projectDto, long userId)
      throws InstanceNotFoundException {
    Optional<Representative> user = representativeDao.findById(userId);
    if(!user.isPresent() || user.get().getEntity().getId()!=projectDto.getEntityId()){
      throw new InstanceNotFoundException("project.entities.entity", projectDto.getEntityId());
    }
    Optional<CollaborationArea> collaborationArea = collaborationAreaDao.findById(projectDto.getAreaId());
    if(!collaborationArea.isPresent()){
      throw new InstanceNotFoundException("project.entities.collaborationArea", projectDto.getAreaId());
    }

    Set<Ods> odsList = projectDto.getOds().stream()
        .map(odsId -> odsDao.findById(odsId))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
    if(odsList.isEmpty()){
      throw new InstanceNotFoundException("project.entities.ods",projectDto.getOds().get(0));
    }

    Project project = new Project(projectDto.getName(),projectDto.getShortDescription(),projectDto.getLongDescription(),
        projectDto.getLocality(),projectDto.getSchedule(),projectDto.getCapacity(),projectDto.getPreferableVolunteer(),
        projectDto.isAreChildren(),projectDto.isVisible(),entidadDao.findById(projectDto.getEntityId()).get(),collaborationArea.get());
    project.setOds(odsList);
    project = projectDao.save(project);
    List<Task> taskList = new ArrayList<>();
    for (String name : projectDto.getTasks()){
      taskList.add(taskDao.save(new Task(name,project)));
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
    Iterable<CollaborationArea> areas = collaborationAreaDao.findAll(Sort.by(Sort.Direction.ASC, "name"));
    List<CollaborationArea> areasAsList = new ArrayList<>();

    areas.forEach(areasAsList::add);

    return areasAsList;
  }

  @Override
  public Block<Project> findProjectsBy(ProjectFiltersDto projectFiltersDto,
                                       PageableDto pageableDto) {
    String[] allowedSortColumns = {"name", "locality", "collaborationAreaId"};
    Pageable pageable = pageableDtoToPageable(pageableDto,allowedSortColumns);
    Page<Project> projectsPage = projectDao.findAll(
        ProjectSpecifications.searchProjects(projectFiltersDto.getName(), projectFiltersDto.getLocality(), projectFiltersDto.getCollaborationAreaId()), pageable);

    return new Block<>(projectsPage.getContent(),projectsPage.hasNext());
  }

  @Override
  public ResourceWithType getLogo(Long entityId) throws InstanceNotFoundException {
    Optional<Entidad> entity = entidadDao.findById(entityId);
    if(!entity.isPresent()){
      throw new InstanceNotFoundException("project.entities.entity", entityId);
    }
    File file = entity.get().getLogo();
    if(file==null){
      throw new InstanceNotFoundException("project.entities.fileLogo", entityId);
    }
    java.io.File savedFile = new java.io.File("./entities/logos/"+file.getId()+"."+file.getExtension());

    Resource resource;
    try {
       resource = new UrlResource(savedFile.toURI());
    } catch (MalformedURLException e) {
      throw new InstanceNotFoundException("project.entities.entity", entityId);
    }
    return new ResourceWithType(resource,file.getExtension());
  }

  @Override
  public Project getProject(long projectId) throws InstanceNotFoundException {
    Optional<Project> project = projectDao.findById(projectId);
    if(!project.isPresent()){
      throw new InstanceNotFoundException("project.entities.project", projectId);
    }
    else{
      return project.get();
    }
  }

  @Override
  public Block<Project> getMyEntityProjects(Long representativeId, PageableDto pageableDto)
      throws InstanceNotFoundException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if(!representative.isPresent()){
      throw new InstanceNotFoundException("project.entities.representative",representativeId);
    }
    Page<Project> projectPage = projectDao.findAllByEntityId(representative.get().getEntity().getId(),
        PageRequest.of(pageableDto.getPage(), pageableDto.getSize()));
    return new Block<>(projectPage.getContent(),projectPage.hasNext());
  }

  @Override
  public Block<Participation> findAllPendingParticipation(Long representativeId, PageableDto pageableDto)
      throws InstanceNotFoundException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if(!representative.isPresent()){
      throw new InstanceNotFoundException("project.entities.representative",representativeId);
    }
    Page<Participation> participationPage = participationDao.findAllByProjectEntityIdAndState(
        representative.get().getEntity().getId(),
        Participation.ParticipationState.PENDING,PageRequest.of(pageableDto.getPage(), pageableDto.getSize()));

    return new Block<>(participationPage.getContent(),participationPage.hasNext());
  }

  @Override
  public Block<Participation> findAllProjectParticipation(Long representativeId, Long projectId, PageableDto pageableDto)
      throws InstanceNotFoundException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    if(!representative.isPresent()){
      throw new InstanceNotFoundException("project.entities.representative",representativeId);
    }
    Project project = getProject(projectId);
    if(project.getEntity().getId()!=representative.get().getEntity().getId()){
      // Administrador no podra ver participaciones de otras entidades
      throw new InstanceNotFoundException("project.entities.project", projectId);
    }

    String[] allowedSortColumns = {"state", "volunteerSurname", "volunteerName", "totalHours"};

    Pageable pageable = pageableDtoToPageable(pageableDto, allowedSortColumns);
    Page<Participation> participationPage = participationDao.findAllByProjectId(
        projectId, pageable);

    return new Block<>(participationPage.getContent(),participationPage.hasNext());
  }

  private Pageable pageableDtoToPageable(PageableDto pageableDto, String[] allowedSortColumns){
    boolean isSorted = pageableDto.getSortValue() != null;
    if (pageableDto.getSortValue() != null && !Arrays.asList(allowedSortColumns).contains(pageableDto.getSortValue())) {
      throw new IllegalArgumentException("Invalid sort value");
    }
    Pageable pageable;
    if(isSorted){
      if(pageableDto.getSortOrder().equals("desc")){
        pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(),
            Sort.by(Sort.Direction.DESC, pageableDto.getSortValue()));
      }
      else{
        pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(),
            Sort.by(Sort.Direction.ASC, pageableDto.getSortValue()));
      }
    }
    else{
      pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
    }
    return pageable;
  }


}
