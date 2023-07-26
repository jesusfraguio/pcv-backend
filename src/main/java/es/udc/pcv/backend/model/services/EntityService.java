package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.rest.dtos.EntityDto;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface EntityService {
  Entidad getEntity(Long entityId) throws InstanceNotFoundException;
  File updateMyEntityCertFile(Long representativeId, Long entityId, MultipartFile file)
      throws PermissionException, IOException, InstanceNotFoundException;
  File updateMyEntityLogo(Long representativeId, Long entityId, MultipartFile file)
      throws PermissionException, IOException, InstanceNotFoundException;
  Entidad updateMyEntity(Long representativeId, Long entityId, EntityDto entityDto)
      throws InstanceNotFoundException, PermissionException;
  Block<Entidad> getAllEntities(PageableDto pageableDto);
}
