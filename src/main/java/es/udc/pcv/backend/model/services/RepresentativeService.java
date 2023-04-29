package es.udc.pcv.backend.model.services;


import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.to.ResourceWithType;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import es.udc.pcv.backend.rest.dtos.ProjectFiltersDto;
import java.util.List;
import org.springframework.core.io.Resource;

public interface RepresentativeService {
  Project createProject(ProjectDto projectDto, long userId) throws InstanceNotFoundException;
  Entidad getMyEntity(long userId);
  List<Ods> getAllOds();
  List<CollaborationArea> getAllCollaborationArea();
  Block<Project> findProjectsBy(ProjectFiltersDto projectFiltersDto, PageableDto pageableDto);
  ResourceWithType getLogo(Long entityId) throws InstanceNotFoundException;
}
