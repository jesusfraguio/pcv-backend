package es.udc.pcv.backend.model.services;

import es.udc.pcv.backend.model.entities.Entidad;
import es.udc.pcv.backend.model.entities.EntidadDao;
import es.udc.pcv.backend.model.entities.File;
import es.udc.pcv.backend.model.entities.FileDao;
import es.udc.pcv.backend.model.to.EntityData;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

@Service
@Transactional
public class AdminServiceImpl implements AdminService{

  @Autowired
  private EntidadDao entityDao;

  @Autowired
  private FileDao fileDao;

  @Override
  public Entidad createEntity(EntityData entity) {
    Entidad entidad = new Entidad(entity.getName(), entity.getShortDescription(), entity.getUrl(),
        entity.getAddress(), entity.getEmail(), entity.getPhone(), entity.getCertFile(),entity.getLogo());
    return entityDao.save(entidad);
  }

  @Override
  public Block<Entidad> getEntities(int page, int size) {
    Slice<Entidad> slice = entityDao.findAll(PageRequest.of(page,size));
    List<Entidad> foundEntities = new ArrayList<>(slice.getContent());
    return new Block<>(foundEntities, slice.hasNext());
  }

  @Override
  public File updateEntityLogo(MultipartFile multipartFile, Long entityId) throws IOException {

    String uploadDir = "./entities/logos/";
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
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
    File saved = fileDao.save(new File(randomUIID,new Date(),multipartFile.getOriginalFilename(),File.FileType.LOGO));
    Entidad entity = entityDao.findById(entityId).get();
    entity.setLogo(saved);
    return saved;
  }

  @Override
  public File updateEntityCert(MultipartFile multipartFile, Long entityId) throws IOException{
    String uploadDir = "./entities/certs/";
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    InputStream inputStream = multipartFile.getInputStream();
    Tika tika = new Tika();
    String mimeType = tika.detect(inputStream);
    String extension = mimeType.split("/")[1];
    UUID randomUIID = UUID.randomUUID();
    String fileName = randomUIID.toString();
    Path filePath = Paths.get(uploadDir + fileName + "." + extension);
    if (Files.exists(filePath)) {
      throw new IOException("File already exists: " + filePath); // se podria quitar esta comprobacion para que sea mas eficiente
    }
    Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    File saved = fileDao.save(new File(randomUIID,new Date(),multipartFile.getOriginalFilename(),File.FileType.AGREEMENT_FILE));
    Entidad entity = entityDao.findById(entityId).get();
    entity.setCertFile(saved);
    return saved;
  }
}
