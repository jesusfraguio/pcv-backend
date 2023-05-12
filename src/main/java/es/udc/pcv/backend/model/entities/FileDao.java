package es.udc.pcv.backend.model.entities;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FileDao extends PagingAndSortingRepository<File, Long> {
  Optional<File> findByFileType(File.FileType fileType);
}