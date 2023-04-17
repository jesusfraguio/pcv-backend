package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.EntidadDao;
import es.udc.pcv.backend.model.to.EntityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminServiceImpl implements AdminService{

  @Autowired
  private EntidadDao entityDao;
  @Override
  public Entidad createEntity(EntityData entity) {
    Entidad entidad = new Entidad(entity.getName(), entity.getShortDescription(), entity.getUrl(),
        entity.getAddress(), entity.getEmail(), entity.getPhone(), entity.getCertFile(),entity.getLogo());
    return entityDao.save(entidad);
  }
}
