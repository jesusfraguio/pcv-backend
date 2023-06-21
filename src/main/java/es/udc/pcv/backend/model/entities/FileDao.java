package es.udc.pcv.backend.model.entities;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FileDao extends PagingAndSortingRepository<File, Long> {
  Optional<File> findByFileType(File.FileType fileType);
  Optional<File> findByEntidadAndVolunteerAndFileType(Entidad entidad, Volunteer volunteer, File.FileType fileType);
  Optional<File> findByVolunteerAndFileType(Volunteer volunteer, File.FileType fileType);
  Page<File> findAllByEntidadAndFileType(Entidad entidad, File.FileType fileType, Pageable pageable);
  boolean existsByVolunteerAndFileTypeAndEntidad(Volunteer volunteer, File.FileType fileType, Entidad entidad);
}