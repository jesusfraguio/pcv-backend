package es.udc.pcv.backend.model.services;


import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.Volunteer;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.to.ResourceWithType;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.ProjectFiltersDto;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface RepresentativeService {
  Project createProject(ProjectDto projectDto, long userId) throws InstanceNotFoundException;
  Entidad getMyEntity(long userId);
  List<Ods> getAllOds();
  List<CollaborationArea> getAllCollaborationArea();
  Block<Project> findProjectsBy(ProjectFiltersDto projectFiltersDto, PageableDto pageableDto);
  ResourceWithType getLogo(Long entityId) throws InstanceNotFoundException;
  Project getProject(long projectId) throws InstanceNotFoundException;
  Block<Project> getMyEntityProjects(Long userId, PageableDto pageableDto) throws InstanceNotFoundException;
  Block<Participation> findAllPendingParticipation(Long representativeId, PageableDto pageableDto) throws InstanceNotFoundException;
  Block<Participation> findAllProjectParticipation(Long representativeId, Long projectId, PageableDto pageableDto) throws InstanceNotFoundException;
  File updateVolunteerCertFile(Long userId, Long id, MultipartFile multipartFile)
      throws InstanceNotFoundException, PermissionException, IOException;
  File updateVolunteerHarassmentCert(Long representativeId, Long volunteerId, MultipartFile multipartFile)
      throws InstanceNotFoundException, IOException;
  File updateVolunteerDNI(Long representativeId, Long volunteerId, MultipartFile multipartFile)
      throws InstanceNotFoundException, IOException;
  Block<Volunteer> findMyEntityVolunteers(Long representativeId, PageableDto pageableDto)
      throws InstanceNotFoundException;
}
