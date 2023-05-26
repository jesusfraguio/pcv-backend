package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.exceptions.AlreadyParticipatingException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.InvalidStatusTransitionException;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
import es.udc.pcv.backend.rest.dtos.ParticipationStatusDto;
import java.util.List;

public interface VolunteerService {
  Participation createParticipation(ParticipationDto participationData) throws InstanceNotFoundException,
      AlreadyParticipatingException;

  Block<Participation> findAllMyParticipations(Long userId, PageableDto pageableDto) throws InstanceNotFoundException;

  Participation updateParticipation(Long representativeId, Long id, ParticipationStatusDto statusDto)
      throws InstanceNotFoundException, InvalidStatusTransitionException;
}
