package es.udc.pcv.backend.model.entities;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ParticipationDao extends PagingAndSortingRepository<Participation, Long> {
  Optional<Participation> findByProjectIdAndVolunteerId(Long projectId, Long volunteerId);
  List<Participation> findByProjectEntityId(Long entityId);
  List<Participation> findByProjectId(Long projectId);
  Page<Participation> findAllByProjectEntityId(Long entityId, Pageable pageable);
  Page<Participation> findAllByProjectEntityIdAndState(Long entityId, Participation.ParticipationState state, Pageable pageable);
  Page<Participation> findAllByProjectId(Long projectId, Pageable pageable);
  Page<Participation> findAllByVolunteerId(Long volunteerId, Pageable pageable);
  Page<Participation> findAllByProjectEntity(Entidad entidad, Pageable pageable);
  boolean existsByProjectEntityIdAndVolunteerId(Long projectEntityId, Long volunteerId);
}
