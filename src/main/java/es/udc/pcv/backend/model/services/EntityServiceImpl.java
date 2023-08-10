package es.udc.pcv.backend.model.services;

import static es.udc.pcv.backend.model.services.RepresentativeServiceImpl.getPageable;

import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.EntidadDao;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.FileDao;
import es.udc.pcv.backend.model.entities.Representative;
import es.udc.pcv.backend.model.entities.RepresentativeDao;
import es.udc.pcv.backend.model.exceptions.InstanceNotFoundException;
import es.udc.pcv.backend.model.exceptions.PermissionException;
import es.udc.pcv.backend.rest.dtos.EntityDto;
import es.udc.pcv.backend.rest.dtos.PageableDto;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class EntityServiceImpl implements EntityService {

  @Autowired
  private EntidadDao entityDao;

  @Autowired
  private RepresentativeDao representativeDao;

  @Autowired
  private FileDao fileDao;

  @Value("${file.base-path}")
  private String basePath;

  @Override
  public Entidad getEntity(Long entityId) throws InstanceNotFoundException {
    return entityDao.findById(entityId).orElseThrow(() -> new InstanceNotFoundException("project.entities.entity",entityId));
  }

  @Override
  public File updateMyEntityCertFile(Long representativeId, Long entityId, MultipartFile multipartFile)
      throws PermissionException, IOException, InstanceNotFoundException {
    String uploadDir = basePath+"entities/certs/";
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    Entidad entidad = validateEntity(representativeId,entityId);
    InputStream inputStream = multipartFile.getInputStream();
    Tika tika = new Tika();
    String mimeType = tika.detect(inputStream);
    String extension = mimeType.split("/")[1];
    UUID randomUIID = UUID.randomUUID();
    String fileName = randomUIID.toString();
    Path filePath = Paths.get(uploadDir + fileName + "." + extension);
    if (Files.exists(filePath)) {
      throw new IOException("File already exists: " + filePath);
    }
    Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    File oldFile = entidad.getCertFile();
    if (oldFile != null) {
      Path path = Paths.get(
          uploadDir + oldFile.getId().toString() + "." + oldFile.getExtension());
      fileDao.delete(oldFile);
      try {
        Files.delete(path);
      }catch (Exception e){
        //If there is no old file in disk because it was already deleted (low chances) app will keep going right
      }
    }
    File saved = fileDao.save(new File(randomUIID,new Date(),multipartFile.getOriginalFilename(),
        File.FileType.AGREEMENT_FILE,extension));
    entidad.setCertFile(saved);
    return saved;
  }

  @Override
  public File updateMyEntityLogo(Long representativeId, Long entityId, MultipartFile multipartFile)
      throws PermissionException, IOException, InstanceNotFoundException {
    String uploadDir = basePath+"entities/logos/";
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    Entidad entidad = validateEntity(representativeId,entityId);
    InputStream inputStream = multipartFile.getInputStream();
    Tika tika = new Tika();
    String mimeType = tika.detect(inputStream);
    String extension = mimeType.split("/")[1];
    UUID randomUIID = UUID.randomUUID();
    String fileName = randomUIID.toString();
    Path filePath = Paths.get(uploadDir + fileName + "." + extension);
    if (Files.exists(filePath)) {
      throw new IOException("File already exists: " + filePath);
    }
    Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    File oldFile = entidad.getLogo();
    if (oldFile != null) {
      Path path = Paths.get(
          uploadDir + oldFile.getId().toString() + "." + oldFile.getExtension());
      fileDao.delete(oldFile);
      try {
        Files.delete(path);
      }catch (Exception e){
        //If there is no old file in disk because it was already deleted (low chances) app will keep going right
      }
    }
    File saved = fileDao.save(new File(randomUIID,new Date(),multipartFile.getOriginalFilename(),
        File.FileType.LOGO,extension));
    entidad.setLogo(saved);
    return saved;
  }

  @Override
  public Entidad updateMyEntity(Long representativeId, Long entityId, EntityDto entityDto)
      throws InstanceNotFoundException, PermissionException {
    Entidad entidad = validateEntity(representativeId,entityId);
    return updateEntity(entidad,entityDto);
  }

  @Override
  public Block<Entidad> getAllEntities(PageableDto pageableDto) {
    String[] allowedSortColumns = {"name"};
    Pageable pageable = pageableDtoToPageable(pageableDto, allowedSortColumns);
    Page<Entidad> entityPage = entityDao.findAll(pageable);

    return new Block<>(entityPage.getContent(), entityPage.hasNext());
  }

  @Transactional
  Entidad validateEntity(Long representativeId, Long entityId) throws InstanceNotFoundException, PermissionException {
    Optional<Representative> representative = representativeDao.findById(representativeId);
    Optional<Entidad> entidad = entityDao.findById(entityId);
    if(!entidad.isPresent()){
      throw new InstanceNotFoundException("project.entities.entity",entityId);
    }
    if(representative.get().getEntity().getId() != entityId){
      throw new PermissionException();
    }
    return entidad.get();
  }

  @Transactional
  Entidad updateEntity(Entidad entidad, EntityDto entityDto){
    /*
    Entidad newEntity = new Entidad();
    newEntity.setId(entidad.getId());
    newEntity.setAddress(entityDto.getAddress() != null ? entityDto.getAddress() : entidad.getAddress());
    newEntity.setEmail(entityDto.getEmail() != null ? entityDto.getEmail() : entidad.getEmail());
    newEntity.setName(entityDto.getName() != null ? entityDto.getName() : entidad.getName());
    newEntity.setPhone(entityDto.getPhone() != null ? entityDto.getPhone() : entidad.getPhone());
    newEntity.setShortDescription(entityDto.getShortDescription() != null ?
        entityDto.getShortDescription() : entidad.getShortDescription());
    newEntity.setUrl(entityDto.getUrl() != null ? entityDto.getUrl() : entidad.getUrl());
    newEntity.setLogo(entidad.getLogo());
    newEntity.setCertFile(entidad.getCertFile());
    return entityDao.save(newEntity);
    */
    if (!Objects.equals(entityDto.getAddress(), entidad.getAddress())) {
      entidad.setAddress(entityDto.getAddress());
    }
    if (!Objects.equals(entityDto.getEmail(), entidad.getEmail())) {
      entidad.setEmail(entityDto.getEmail());
    }

    if (!Objects.equals(entityDto.getName(), entidad.getName())) {
      entidad.setName(entityDto.getName());
    }

    if (!Objects.equals(entityDto.getPhone(), entidad.getPhone())) {
      entidad.setPhone(entityDto.getPhone());
    }

    if (!Objects.equals(entityDto.getShortDescription(), entidad.getShortDescription())) {
      entidad.setShortDescription(entityDto.getShortDescription());
    }

    if (!Objects.equals(entityDto.getUrl(), entidad.getUrl())) {
      entidad.setUrl(entityDto.getUrl());
    }
    return entidad;
  }

  private Pageable pageableDtoToPageable(PageableDto pageableDto, String[] allowedSortColumns) {
    return getPageable(pageableDto, allowedSortColumns);
  }

}
