package es.udc.pcv.backend.rest.dtos;

import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.services.Block;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipationConversor {

  @Mapping(target = "projectId", source = "project.id")
  @Mapping(target = "volunteerId", source = "volunteer.id")
  @Mapping(target = "status", source = "state")
  ParticipationDto toParticipationDto(Participation participation);

  @Mapping(target = "projectId", source = "project.id")
  @Mapping(target = "projectName", source = "project.name")
  @Mapping(target = "volunteerId", source = "volunteer.id")
  @Mapping(target = "volunteerName", source = "volunteer.name")
  @Mapping(target = "volunteerSurname", source = "volunteer.surname")
  @Mapping(target = "status", source = "state")
  ParticipationWithUserDto toParticipationWithUserDto(Participation participation);

  @Mapping(target = "projectName", source = "project.name")
  @Mapping(target = "isRecommended", source = "recommended")
  @Mapping(target = "projectId", source = "project.id")
  @Mapping(target = "volunteerId", source = "volunteer.id")
  @Mapping(target = "status", source = "state")
  @Mapping(target = "entityId", source = "project.entity.id")
  ParticipationSummaryDto toParticipationSummaryDto(Participation participation);

  Block<ParticipationSummaryDto> toParticipationBlockDto(Block<Participation> projectBlock);

  Block<ParticipationWithUserDto> toParticipationWithUserBlockDto(Block<Participation> projectBlock);
}
