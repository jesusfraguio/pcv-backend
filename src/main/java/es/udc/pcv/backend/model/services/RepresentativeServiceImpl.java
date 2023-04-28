package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.CollaborationAreaDao;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.EntidadDao;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.OdsDao;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.ProjectDao;
import es.udc.pcv.backend.model.entities.ProjectSpecifications;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.entities.Task;
import es.udc.pcv.backend.model.entities.TaskDao;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.ProjectFiltersDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
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
    boolean isSorted = pageableDto.getSortValue() != null;
    if (pageableDto.getSortValue() != null && !Arrays.asList(allowedSortColumns).contains(pageableDto.getSortValue())) {
      throw new IllegalArgumentException("Invalid sort value");
    }
    Pageable pageable;
    if(isSorted){
      if(pageableDto.getSortValue()!=null && pageableDto.getSortValue().equals("desc")){
        pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(),
            Sort.by(pageableDto.getSortValue(), "desc"));
      }
      else{
        pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(),
            Sort.by(pageableDto.getSortValue(), "asc"));
      }
    }
    else{
      pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
    }
    Page<Project> projectsPage = projectDao.findAll(
        ProjectSpecifications.searchProjects(projectFiltersDto.getName(), projectFiltersDto.getLocality(), projectFiltersDto.getCollaborationAreaId()), pageable);

    return new Block<>(projectsPage.getContent(),projectsPage.hasNext());
  }


}
