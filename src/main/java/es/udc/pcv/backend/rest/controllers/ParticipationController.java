package es.udc.pcv.backend.rest.controllers;

import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.VolunteerService;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationConversor;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
import es.udc.pcv.backend.rest.dtos.ParticipationSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
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
}