package es.udc.pcv.backend.model.entities;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RepresentativeDao extends PagingAndSortingRepository<Representative, Long> {
  List<Representative> findByEntityId(long entityId);
}
