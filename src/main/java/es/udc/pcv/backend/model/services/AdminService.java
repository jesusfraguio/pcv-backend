package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.to.EntityData;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
  Entidad createEntity(EntityData entity);
  Block<Entidad> getEntities(int page, int size);
  File updateEntityLogo(MultipartFile multipartFile, Long entityId) throws IOException;
  File updateEntityCert(MultipartFile multipartFile, Long entityId) throws IOException;
  boolean updateProjectOds(Long projectId, List<Long> ods);
}
