package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.to.EntityData;

public interface AdminService {
  Entidad createEntity(EntityData entity);
}
