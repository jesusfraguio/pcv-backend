package es.udc.pcv.backend.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.InvalidStatusTransitionException;
import es.udc.pcv.backend.model.exceptions.ParticipationIsInDateException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.services.VolunteerService;
import es.udc.pcv.backend.rest.dtos.HourVolunteerDto;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationConversor;
import es.udc.pcv.backend.rest.dtos.ParticipationStatusDto;
import es.udc.pcv.backend.rest.dtos.ParticipationSummaryDto;
import es.udc.pcv.backend.rest.dtos.ParticipationWithUserDto;
import es.udc.pcv.backend.rest.dtos.RegisteredHoursDto;
import es.udc.pcv.backend.rest.dtos.SelectorDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/participation")
@Tag(name = "participation")
public class ParticipationController {

  @Autowired
  private ObjectMapper objectMapper;

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
  @GetMapping("/projects/{projectId}")
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
  @Operation(summary = "Creates a new certificate file (user)")
  @RequestMapping(value = "/certFiles", method = RequestMethod.POST,  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public boolean addCertFile( @RequestAttribute long userId,
      @RequestParam String participationNumber, @RequestPart(name="cert",required = true)
      MultipartFile cert)
      throws IOException, PermissionException, InstanceNotFoundException,
      InvalidStatusTransitionException {
    Long realParticipationNumber = objectMapper.readValue(participationNumber, Long.class);

    File certFile = null;
    if(cert!= null){
      certFile = volunteerService.updateMyParticipationCertFile(userId,realParticipationNumber,cert);
    }
    return true;

  }
  @Operation(summary = "Creates entity's volunteer certificate file")
  @RequestMapping(value = "/certFiles/representative", method = RequestMethod.POST,  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public boolean addVolunteerCertFile(@RequestAttribute long userId,
                              @RequestParam String participationNumber, @RequestPart(name="cert",required = true)
                                  MultipartFile cert)
      throws IOException, PermissionException, InstanceNotFoundException {
    Long realParticipationNumber = objectMapper.readValue(participationNumber, Long.class);

    File certFile = null;
    if(cert!= null){
      certFile = representativeService.updateVolunteerCertFile(userId,realParticipationNumber,cert);
    }
    return true;

  }
  @Operation(summary = "Get a list with all entity's projects (id,name)")
  @GetMapping(value = "/projects")
  public List<SelectorDataDto> getAllMyProjects(@RequestAttribute Long userId)
      throws InstanceNotFoundException {
    return representativeService.findAllMyEntityProjects(userId);
  }
  @Operation(summary = "Get a list with all project's participations (id,name)")
  @GetMapping(value = "/projects/{projectId}/participation")
  public List<SelectorDataDto> getAllMyProjectParticipations(@RequestAttribute Long userId, @PathVariable Long projectId)
      throws InstanceNotFoundException, PermissionException {
    return representativeService.findAllProjectParticipations(userId,projectId);
  }

  @Operation(summary = "Creates a new hour register  participation")
  @PostMapping(value = "/hourRegisters")
  public RegisteredHoursDto createHourRegister(@RequestAttribute Long userId, @Validated @RequestBody
      RegisteredHoursDto registeredHoursDto)
      throws InstanceNotFoundException, ParticipationIsInDateException {
    return participationConversor.toRegisteredHoursDto(representativeService.createHourRegister(userId, registeredHoursDto));
  }
  @Operation(summary = "Gets all participation's hours register of my entity between two dates and a project (optional)")
  @GetMapping(value = "/hourRegisters")
  public List<RegisteredHoursDto> getAllRegisteredHours(@RequestAttribute Long userId,
                                                        @RequestParam(required = false) Long projectId,
                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate)
      throws PermissionException, InstanceNotFoundException {
    return participationConversor.toRegisteredHoursListDto(representativeService.findAllHoursWithinDates(userId,
        projectId,LocalDate.parse(startDate),LocalDate.parse(endDate)));
  }
  @Operation(summary = "Deletes one hour register participation")
  @DeleteMapping(value = "/hourRegisters/{hourRegisterId}")
  public boolean deleteHourRegister(@RequestAttribute Long userId, @PathVariable Long hourRegisterId)
      throws InstanceNotFoundException {
    return representativeService.deleteHourRegister(userId,hourRegisterId);
  }

  @Operation(summary = "Gets total hours each volunteer has done in a project of a certain year(1st January - 31th December)")
  @GetMapping(value="/projects/{projectId}/totalHours/{year}")
  public List<HourVolunteerDto> getTotalHours(@RequestAttribute Long userId, @PathVariable Long projectId,
                                              @PathVariable Integer year,
                                              @RequestParam String volunteerIds)
      throws InstanceNotFoundException {
    List<Long> volunteerList = Arrays.stream(volunteerIds.split(","))
        .map(Long::valueOf)
        .collect(Collectors.toList());
    if(volunteerList.size()<=0 || volunteerList.size()>20){
      throw new ValidationException();
    }
    return representativeService.getTotalHours(userId,year,projectId,volunteerList);
  }
}