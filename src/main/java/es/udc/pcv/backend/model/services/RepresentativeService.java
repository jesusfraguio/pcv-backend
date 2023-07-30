package es.udc.pcv.backend.model.services;


import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.RegisteredHours;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.ParticipationIsInDateException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.to.ResourceWithType;
import es.udc.pcv.backend.rest.dtos.HourVolunteerDto;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.ProjectFiltersDto;
import es.udc.pcv.backend.rest.dtos.RegisteredHoursDto;
import es.udc.pcv.backend.rest.dtos.SelectorDataDto;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface RepresentativeService {
  Project createProject(ProjectDto projectDto, long userId) throws InstanceNotFoundException;
  Entidad getMyEntity(long userId);
  List<Ods> getAllOds();
  List<CollaborationArea> getAllCollaborationArea();
  Block<Project> findProjectsBy(ProjectFiltersDto projectFiltersDto, PageableDto pageableDto);
  ResourceWithType getLogo(Long entityId) throws InstanceNotFoundException;
  ResourceWithType getAgreementFile(Long entityId) throws InstanceNotFoundException;
  ResourceWithType getVolunteerFile(Long representativeId, Long volunteerId, String fileType) throws InstanceNotFoundException;
  Project getProject(long projectId) throws InstanceNotFoundException;
  Block<Project> getMyEntityProjects(Long userId, Long entityId, PageableDto pageableDto) throws InstanceNotFoundException;
  Block<Participation> findAllPendingParticipation(Long representativeId, PageableDto pageableDto) throws InstanceNotFoundException;
  Block<Participation> findAllProjectParticipation(Long representativeId, Long projectId, PageableDto pageableDto) throws InstanceNotFoundException;
  File updateVolunteerCertFile(Long userId, Long id, MultipartFile multipartFile)
      throws InstanceNotFoundException, PermissionException, IOException;
  File updateVolunteerHarassmentCert(Long representativeId, Long volunteerId, MultipartFile multipartFile)
      throws InstanceNotFoundException, IOException;
  File updateVolunteerDNI(Long representativeId, Long volunteerId, MultipartFile multipartFile)
      throws InstanceNotFoundException, IOException;
  File uploadVolunteerPhoto(Long userId, Long id, MultipartFile multipartFile)
      throws InstanceNotFoundException, IOException;
  Block<Volunteer> findMyEntityVolunteers(Long representativeId, PageableDto pageableDto)
      throws InstanceNotFoundException;
  List<RegisteredHours> findAllHoursWithinDates(Long representativeId, Long projectId, LocalDate startDate, LocalDate endDate)
      throws InstanceNotFoundException, PermissionException;
  List<SelectorDataDto> findAllMyEntityProjects(Long representativeId)
      throws InstanceNotFoundException;
  List<SelectorDataDto> findAllProjectParticipations(Long representativeId, Long projectId)
      throws InstanceNotFoundException, PermissionException;
  RegisteredHours createHourRegister(Long representativeId, RegisteredHoursDto registeredHoursDto)
      throws InstanceNotFoundException, ParticipationIsInDateException;
  boolean deleteHourRegister(Long representativeId, Long hourRegisterId)
      throws InstanceNotFoundException;
  List<HourVolunteerDto> getTotalHours(Long representativeId, Integer year, Long projectId, List<Long> volunteerId)
      throws InstanceNotFoundException;
}
