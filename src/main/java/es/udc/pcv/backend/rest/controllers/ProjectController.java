package es.udc.pcv.backend.rest.controllers;

import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.User;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.exceptions.DuplicateInstanceException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.UserService;
import es.udc.pcv.backend.model.to.UserWithRepresentative;
import es.udc.pcv.backend.model.to.UserWithVolunteer;
import es.udc.pcv.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.pcv.backend.rest.dtos.EntityConversor;
import es.udc.pcv.backend.rest.dtos.MessageDTO;
import es.udc.pcv.backend.rest.dtos.OdsWithCollaborationAreaDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.RepresentativeDto;
import es.udc.pcv.backend.rest.dtos.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/projects")
@Tag(name = "projects")
public class ProjectController {

  @Autowired
  private RepresentativeService representativeService;

  @Autowired
  private EntityConversor entityConversor;

  @Operation(summary = "create a project")
  @PostMapping("/createProject")
  public ResponseEntity<ProjectDto> createProject(
      @Validated({ProjectDto.AllValidations.class}) @RequestAttribute Long userId, @RequestBody ProjectDto projectDto)
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
}
