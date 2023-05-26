package es.udc.pcv.backend.rest.controllers;

import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.InvalidStatusTransitionException;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.VolunteerService;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationConversor;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
import es.udc.pcv.backend.rest.dtos.ParticipationStatusDto;
import es.udc.pcv.backend.rest.dtos.ParticipationSummaryDto;
import es.udc.pcv.backend.rest.dtos.ParticipationWithUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/participation")
@Tag(name = "participations")
public class ParticipationController {

  @Autowired
  private RepresentativeService representativeService;

  @Autowired
  private VolunteerService volunteerService;

  @Autowired
  private ParticipationConversor participationConversor;

  @Operation(summary = "get a block of my participations")
  @GetMapping("/my")
  public Block<ParticipationSummaryDto> searchMyParticipations(@RequestAttribute long userId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(required = false) String sortValue,
                                                               @RequestParam(required = false) String sortOrder)
      throws InstanceNotFoundException {
    PageableDto pageableDto = new PageableDto(page, size, sortValue, sortOrder);
    return participationConversor.toParticipationBlockDto(
        volunteerService.findAllMyParticipations(userId, pageableDto));
  }

  @Operation(summary = "update participation status")
  @PatchMapping("/{id}")
  public ParticipationWithUserDto updateParticipationStatus(@RequestAttribute long userId, @PathVariable long id,
                                                            @RequestBody ParticipationStatusDto participationData)
      throws InstanceNotFoundException, InvalidStatusTransitionException {
    return participationConversor.toParticipationWithUserDto(volunteerService.updateParticipation(userId,id,participationData));
  }

  @Operation(summary = "get all pending users participations")
  @GetMapping("/getAllPendingParticipations")
  public Block<ParticipationWithUserDto> getAllPending(@RequestAttribute long userId, @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String sortValue,
                                                       @RequestParam(required = false) String sortOrder)
      throws InstanceNotFoundException {
    PageableDto pageableDto = new PageableDto(page, size, sortValue, sortOrder);
    return participationConversor.toParticipationWithUserBlockDto(
        representativeService.findAllPendingParticipation(userId,pageableDto));
  }

  @Operation(summary = "get all user participation with their names of a project")
  @GetMapping("/getAllProjectParticipations/{projectId}")
  public Block<ParticipationWithUserDto> getAllProjectParticipations(@RequestAttribute long userId, @PathVariable Long projectId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(required = false) String sortValue,
                                                                     @RequestParam(required = false) String sortOrder)
      throws InstanceNotFoundException {
    PageableDto pageableDto = new PageableDto(page, size, sortValue, sortOrder);
    return participationConversor.toParticipationWithUserBlockDto(
        representativeService.findAllProjectParticipation(userId,projectId,pageableDto));
  }
}