package es.udc.pcv.backend.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.model.services.Block;
import es.udc.pcv.backend.model.services.EntityService;
import es.udc.pcv.backend.model.services.RepresentativeService;
import es.udc.pcv.backend.model.to.ResourceWithType;
import es.udc.pcv.backend.rest.dtos.EntityConversor;
import es.udc.pcv.backend.rest.dtos.EntityDto;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import es.udc.pcv.backend.rest.dtos.SelectorDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/entities")
@Tag(name = "entities")
public class EntityController {

  @Autowired
  private RepresentativeService representativeService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EntityService entityService;

  @Autowired
  private EntityConversor entityConversor;

  @Operation(summary = "get the image(logo) of an entity")
  @GetMapping("/{id}/getLogo")
  public ResponseEntity<Resource> getLogo(@PathVariable Long id)
      throws InstanceNotFoundException {
    ResourceWithType resource = representativeService.getLogo(id);
    MediaType mediaType;
    if (resource.getExtension().equals("png")) {
      mediaType = MediaType.IMAGE_PNG;
    } else if (resource.getExtension().equals("gif")) {
      mediaType = MediaType.IMAGE_GIF;
    } else mediaType = MediaType.IMAGE_JPEG;
    return ResponseEntity.ok()
        .contentType(mediaType)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + resource.getResource().getFilename() + "\"")
        .body(resource.getResource());
  }

  @Operation(summary = "get an entity's agreement file (only logged users)")
  @GetMapping("/{id}/getAgreementFile")
  public ResponseEntity<Resource> getAgreementFile(@RequestAttribute Long userId,
                                                   @PathVariable Long id)
      throws InstanceNotFoundException {
    ResourceWithType resource = representativeService.getAgreementFile(id);
    MediaType mediaType;
    if (resource.getExtension().equals("pdf")) {
      mediaType = MediaType.APPLICATION_PDF;
    } else mediaType = MediaType.APPLICATION_OCTET_STREAM;
    return ResponseEntity.ok()
        .contentType(mediaType)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + "CopiaDeArchivoCompromiso" + "\"")
        .body(resource.getResource());
  }

  @Operation(summary = "gets all entity's info")
  @GetMapping("/{id}")
  public EntityDto getEntity(@PathVariable Long id) throws InstanceNotFoundException {
    return entityConversor.toEntityDto(entityService.getEntity(id));
  }

  @Operation(summary = "allows upload new entity files and also update an entity")
  @RequestMapping(value = "/{entityId}/update", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public EntityDto updateEntity(@RequestAttribute Long userId, @PathVariable Long entityId,
                                @RequestParam String entityDto,
                                @RequestPart(name = "logo", required = false) MultipartFile logo,
                                @RequestParam(name = "cert", required = false) MultipartFile cert)
      throws PermissionException, InstanceNotFoundException, IOException {
    EntityDto returnEntity = null;
    if (cert != null) {
      entityService.updateMyEntityCertFile(userId, entityId, cert);
    }
    if (logo != null) {
      entityService.updateMyEntityLogo(userId, entityId, logo);
    }
    if (entityDto != null) {
      EntityDto entityToUpdate = objectMapper.readValue(entityDto, EntityDto.class);
      return entityConversor.toEntityDto(
          entityService.updateMyEntity(userId, entityId, entityToUpdate));
    } else {
      return getEntity(entityId);
    }

  }

  @Operation(summary = "gets a list with entity's name and id")
  @GetMapping(value = "/getAllEntitiesSelector")
  Block<SelectorDataDto> getAllEntitiesSelector(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "500") int size,
                                                @RequestParam(required = false) String sortValue,
                                                @RequestParam(required = false) String sortOrder){
    PageableDto pageableDto = new PageableDto(page,size,sortValue,sortOrder);
    return entityConversor.toSelectorDataDtoBlock(entityService.getAllEntities(pageableDto));
  }
}

