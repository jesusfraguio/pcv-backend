package es.udc.pcv.backend.test.model.services;

import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.Task;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.services.AdminService;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.UserService;
import es.udc.pcv.backend.model.to.EntityData;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.ProjectFiltersDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RepresentativeServiceTest {
  private final Long NON_EXISTENT_ID = Long.valueOf(-1);

  @Autowired
  private UserService userService;

  @Autowired
  private RepresentativeService representativeService;

  @Autowired
  private AdminService adminService;

  private User createUser(String email) {
    return new User("password",email);
  }

  private Representative createRepresentative() {
    Entidad entidad = adminService.createEntity(new EntityData("Cáritas","Description","caritas.es","rua","mail@caritas.es",null,null,null));
    return new Representative("name","surname","600999999", entidad);
  }

  private ProjectDto createProjectDto(String name, List<String> task, List<Long> ods,long entityId, long areaId){
    ProjectDto projectDto = new ProjectDto(null,name,"Descripción corta","Detalle de las tareas","A Coruña","Lunes y Martes de 10:30 a 12:30",
        10,"Sin preferencias",false,true,task,ods,entityId,areaId);
    return projectDto;
  }

  @Test
  public void createProject() throws InstanceNotFoundException, DuplicateInstanceException {
    User user = createUser("representante6@gmail.com");
    Representative representative = createRepresentative();
    long userId = userService.createRepresentative(new UserWithRepresentative(user.getPassword(),user.getEmail(),representative.getName(),
        representative.getSurname(),representative.getPhone(),representative.getEntity().getId())).getId();
    List<Ods> odsList = representativeService.getAllOds();
    List<CollaborationArea> collaborationAreas = representativeService.getAllCollaborationArea();
    List<String> tasks = new ArrayList<>();
    List<Long> itemIds = odsList.stream()
        .filter(item -> (item.getNumber() == 2) || (item.getNumber() == 1))
        .map(Ods::getId)
        .collect(Collectors.toList());
    tasks.add("Task 1");
    tasks.add("Cocinar y preparar comidas");
    tasks.add("Envasar y distribuir alimentos a personas necesitadas");
    ProjectDto projectDto = createProjectDto("Proyecto X",tasks,itemIds,representative.getEntity().getId(),collaborationAreas.get(0).getId());
    Project project = representativeService.createProject(projectDto,userId);

    Set<Long> savedOdsNumbers = project.getOds().stream()
        .map(Ods::getId)
        .collect(Collectors.toSet());
    List<String> savedTasks = project.getTasks().stream().map(Task::getName).collect(Collectors.toList());
    assertEquals(project.getCapacity(),projectDto.getCapacity());
    assertEquals(project.getCollaborationArea().getId(),projectDto.getAreaId());
    assertTrue(savedOdsNumbers.containsAll(itemIds));
    assertTrue(savedTasks.containsAll(tasks));
  }

  @Test
  public void findProjects() throws InstanceNotFoundException, DuplicateInstanceException{
    User user = createUser("representante6@gmail.com");
    Representative representative = createRepresentative();
    long userId = userService.createRepresentative(new UserWithRepresentative(user.getPassword(),user.getEmail(),representative.getName(),
        representative.getSurname(),representative.getPhone(),representative.getEntity().getId())).getId();
    List<Ods> odsList = representativeService.getAllOds();
    List<CollaborationArea> collaborationAreas = representativeService.getAllCollaborationArea();
    List<String> tasks = new ArrayList<>();
    List<Long> itemIds = odsList.stream()
        .filter(item -> (item.getNumber() == 2) || (item.getNumber() == 1))
        .map(Ods::getId)
        .collect(Collectors.toList());
    tasks.add("Task 1");
    tasks.add("Cocinar y preparar comidas");
    tasks.add("Envasar y distribuir alimentos a personas necesitadas");
    ProjectDto projectDto = createProjectDto("Proyecto 1",tasks,itemIds,representative.getEntity().getId(),collaborationAreas.get(0).getId());
    ProjectDto projectDto2 = createProjectDto("Proyecto 2",tasks,itemIds,representative.getEntity().getId(),collaborationAreas.get(0).getId());
    ProjectDto projectDto3 = createProjectDto("Proyecto 3",tasks,itemIds,representative.getEntity().getId(),collaborationAreas.get(0).getId());
    Project project1 = representativeService.createProject(projectDto,userId);
    Project project2 = representativeService.createProject(projectDto2,userId);
    Project project3 = representativeService.createProject(projectDto3,userId);
    List<Project> expectedResult1 = Arrays.asList(project1,project2,project3);
    Block<Project> projectBlock = representativeService.findProjectsBy(
        new ProjectFiltersDto("Proyecto",null,collaborationAreas.get(0).getId()),
        new PageableDto(0,3,null,null));

    assertTrue(projectBlock.getItems().containsAll(expectedResult1));

    Block<Project> projectBlock2 = representativeService.findProjectsBy(
        new ProjectFiltersDto("Proyecto 1",null,null),
        new PageableDto(0,3,null,null));
    assertTrue(projectBlock2.getItems().containsAll(Arrays.asList(project1)));
  }
}
