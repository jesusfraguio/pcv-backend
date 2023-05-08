package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;

public interface VolunteerService {
  Participation createParticipation(ParticipationDto participationData) throws InstanceNotFoundException;
}
