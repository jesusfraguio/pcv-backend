package es.udc.pcv.backend.model.services;


import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.rest.dtos.ProjectDto;
import java.util.List;

public interface RepresentativeService {
  Project createProject(ProjectDto projectDto, long userId) throws InstanceNotFoundException;
  List<Ods> getAllOds();
  List<CollaborationArea> getAllCollaborationArea();
}
