package es.udc.pcv.backend.rest.dtos;

import es.udc.pcv.backend.model.entities.CollaborationArea;
import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.Ods;
import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.entities.Project;
import es.udc.pcv.backend.model.entities.Task;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.to.EntityData;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EntityConversor {
  EntityData toEntity(EntityDto entityDto);

  //@BeanMapping(ignoreByDefault = true)
  @Mapping(target = "logoName", ignore = true)
  @Mapping(target = "certName", ignore = true)
  EntityDto toEntityDto(Entidad entity);

  List<EntityDto> toEntityListDto(List<Entidad> entity);

  @Named("mapTasksToStrings")
  default List<String> mapTasksToStrings(List<Task> tasks) {
    return tasks.stream()
        .map(Task::getName)
        .collect(Collectors.toList());
  }

  @Named("mapOdsToIds")
  default List<Long> mapOdsToIds(Set<Ods> ods) {
    return ods.stream()
        .map(Ods::getId)
        .collect(Collectors.toList());
  }


  @Mapping(target = "tasks", source = "tasks", qualifiedByName = "mapTasksToStrings")
  @Mapping(target = "entityId", source = "entity.id")
  @Mapping(target = "areaId", source = "collaborationArea.id")
  @Mapping(target = "ods", source = "ods", qualifiedByName = "mapOdsToIds")
  @Mapping(target = "entityName", source="entity.name")
  @Mapping(target = "areaName", source = "collaborationArea.name")
  ProjectDto toProjectDto(Project project);

  @Mapping(target = "entityName", source = "entity.name")
  @Mapping(target = "areaName", source = "collaborationArea.name")
  @Mapping(target = "entityId", source = "entity.id")
  @Mapping(target = "areaId", source = "collaborationArea.id")
  ProjectSummaryDto toProjectSummaryDto(Project project);

  Block<ProjectSummaryDto> toProjectBlockDto(Block<Project> projectBlock);

  List<OdsSummaryDTO> toOdsSummaryDto(List<Ods> ods);

  List<CollaborationAreaDTO> toCollaborationAreaDTO(List<CollaborationArea> collaborationAreas);

}
