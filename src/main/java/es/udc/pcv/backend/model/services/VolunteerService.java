package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.Participation;
import es.udc.pcv.backend.model.exceptions.AlreadyParticipatingException;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.InvalidStatusTransitionException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.ParticipationDto;
import es.udc.pcv.backend.rest.dtos.ParticipationStatusDto;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface VolunteerService {
  Participation createParticipation(ParticipationDto participationData) throws InstanceNotFoundException,
      AlreadyParticipatingException;

  Block<Participation> findAllMyParticipations(Long userId, PageableDto pageableDto) throws InstanceNotFoundException;

  Participation updateParticipation(Long representativeId, Long id, ParticipationStatusDto statusDto)
      throws InstanceNotFoundException, InvalidStatusTransitionException;

  File updateMyParticipationCertFile(Long userId, Long id, MultipartFile multipartFile)
      throws InstanceNotFoundException, PermissionException, IOException;
}
