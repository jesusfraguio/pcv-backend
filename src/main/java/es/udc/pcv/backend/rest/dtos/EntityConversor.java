package es.udc.pcv.backend.rest.dtos;

import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.to.EntityData;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityConversor {
  EntityData toEntity(EntityDto entityDto);

  //@BeanMapping(ignoreByDefault = true)
  @Mapping(target = "logoName", ignore = true)
  @Mapping(target = "certName", ignore = true)
  EntityDto toEntityDto(Entidad entity);

  @BeanMapping(ignoreByDefault = true)
  List<EntityDto> toEntityListDto(List<Entidad> entity);
}
