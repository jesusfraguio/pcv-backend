package es.udc.pcv.backend.rest.controllers;

import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.exceptions.AlreadyParticipatingException;
import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.exceptions.ProjectIsPausedException;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.UserService;
import es.udc.pcv.backend.model.services.VolunteerService;
import es.udc.pcv.backend.model.to.ResourceWithType;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.pcv.backend.rest.dtos.EntityConversor;
import es.udc.pcv.backend.rest.dtos.MessageDTO;
import es.udc.pcv.backend.rest.dtos.OdsWithCollaborationAreaDto;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationConversor;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.ProjectFiltersDto;
import es.udc.pcv.backend.rest.dtos.ProjectSummaryDto;
import es.udc.pcv.backend.rest.dtos.RepresentativeDto;
import es.udc.pcv.backend.rest.dtos.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/projects")
@Tag(name = "projects")
public class ProjectController {

  @Autowired
  private RepresentativeService representativeService;

  @Autowired
  private VolunteerService volunteerService;

  @Autowired
  private EntityConversor entityConversor;

  @Autowired
  private ParticipationConversor participationConversor;

  @Operation(summary = "create a project")
  @PostMapping("/createProject")
  public ResponseEntity<ProjectDto> createProject(
      @RequestAttribute Long userId, @Validated({ProjectDto.AllValidations.class}) @RequestBody ProjectDto projectDto)
      throws InstanceNotFoundException {

    ProjectDto savedProjectDto = entityConversor.toProjectDto(representativeService.createProject(projectDto,userId));

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(savedProjectDto.getId()).toUri();

    return ResponseEntity.created(location).body(savedProjectDto);

  }
  @Operation(summary = "updates a project")
  @PostMapping("/updateProject")
  public ResponseEntity<ProjectDto> updateProject(
      @RequestAttribute Long userId, @Validated({ProjectDto.UpdateValidation.class}) @RequestBody ProjectDto projectDto)
      throws InstanceNotFoundException {

    ProjectDto savedProjectDto = entityConversor.toProjectDto(representativeService.createProject(projectDto,userId));

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(projectDto.getId()).toUri();

    return ResponseEntity.created(location).body(savedProjectDto);

  }
  @Operation(summary = "get a list of ods and areas")
  @GetMapping("/getSummaryOdsAndCollaborationArea")
  public OdsWithCollaborationAreaDto getSummaryOdsAndAreas(){
    OdsWithCollaborationAreaDto ret = new OdsWithCollaborationAreaDto();
    ret.setOdsSummary(entityConversor.toOdsSummaryDto(representativeService.getAllOds()));
    ret.setAreaList(entityConversor.toCollaborationAreaDTO(representativeService.getAllCollaborationArea()));
    return ret;
  }


  @Operation(summary = "get a block of projects(without images for performance)")
  @GetMapping("/searchProjectsBy")
  public Block<ProjectSummaryDto> searchProjectsBy(@RequestParam(required = false) String name, @RequestParam(required = false) String locality,
                                                       @RequestParam(required = false) Long collaborationAreaId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String sortValue,
                                                       @RequestParam(required = false) String sortOrder){
    ProjectFiltersDto projectFiltersDto = new ProjectFiltersDto(name,locality,collaborationAreaId);
    PageableDto pageableDto = new PageableDto(page,size,sortValue,sortOrder);
    return entityConversor.toProjectBlockDto(representativeService.findProjectsBy(projectFiltersDto,pageableDto));
  }

  @Operation(summary = "get all detailed info of a project")
  @GetMapping("/project/{projectId}")
  public ProjectDto getProjectInfo(@PathVariable Long projectId) throws InstanceNotFoundException {
    return entityConversor.toProjectDto(representativeService.getProject(projectId));
  }

  @Operation(summary = "get the image(logo) of an entity")
  @GetMapping("/getLogo")
  public ResponseEntity<Resource> getLogo(@RequestParam Long entityId) throws InstanceNotFoundException{
    ResourceWithType resource = representativeService.getLogo(entityId);
    MediaType mediaType;
    if(resource.getExtension().equals("png")){
      mediaType = MediaType.IMAGE_PNG;
    }
    else if(resource.getExtension().equals("gif")){
      mediaType = MediaType.IMAGE_GIF;
    }
    else mediaType = MediaType.IMAGE_JPEG;
    return ResponseEntity.ok()
        .contentType(mediaType)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getResource().getFilename() + "\"")
        .body(resource.getResource());
  }

  @Operation(summary = "get an entity's agreement file (only logged users)")
  @GetMapping("/getAgreementFile/{id}")
  public ResponseEntity<Resource> getAgreementFile(@RequestAttribute Long userId, @PathVariable Long id) throws InstanceNotFoundException {
    ResourceWithType resource = representativeService.getAgreementFile(id);
    MediaType mediaType;
    if(resource.getExtension().equals("pdf")){
      mediaType = MediaType.APPLICATION_PDF;
    }
    else mediaType = MediaType.APPLICATION_OCTET_STREAM;
    return ResponseEntity.ok()
        .contentType(mediaType)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "CopiaDeArchivoCompromiso" + "\"")
        .body(resource.getResource());
  }

  @Operation(summary= "get my entity's projects")
  @GetMapping("/myEntityProjects")
  public Block<ProjectSummaryDto> getMyEntityProjects(@RequestAttribute long userId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String sortValue,
                                                      @RequestParam(required = false) String sortOrder)
      throws InstanceNotFoundException {
    PageableDto pageableDto = new PageableDto(page,size,sortValue,sortOrder);
    return entityConversor.toProjectBlockDto(representativeService.getMyEntityProjects(userId,pageableDto));
  }

  @Operation(summary = "create a participation for myself (the user who is requesting)")
  @PostMapping("/createMyParticipation")
  public ResponseEntity<ParticipationDto> createMyParticipation(
      @Validated({ParticipationDto.AllValidations.class}) @RequestBody ParticipationDto participationDto, @RequestAttribute Long userId)
      throws InstanceNotFoundException, AlreadyParticipatingException, PermissionException,
      ProjectIsPausedException {

    ParticipationDto savedParticipationDto = participationConversor.toParticipationDto(volunteerService.createMyParticipation(participationDto, userId));

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(participationDto.getId()).toUri();

    return ResponseEntity.created(location).body(savedParticipationDto);

  }

  @Operation(summary = "create a participation for my volunteer" )
  @PostMapping("/representative/createParticipation")
  public ResponseEntity<ParticipationDto> addVolunteerToProject(
      @Validated({ParticipationDto.AllValidations.class}) @RequestBody ParticipationDto participationDto,
      @RequestAttribute Long userId)
      throws InstanceNotFoundException, AlreadyParticipatingException, PermissionException {

    ParticipationDto savedParticipationDto = participationConversor.toParticipationDto(volunteerService.createParticipation(participationDto,userId));

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(participationDto.getId()).toUri();

    return ResponseEntity.created(location).body(savedParticipationDto);

  }
}
