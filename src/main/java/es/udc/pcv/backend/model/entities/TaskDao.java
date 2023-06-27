package es.udc.pcv.backend.model.entities;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface TaskDao extends PagingAndSortingRepository<Task, Long> {
  void deleteAllByProjectId(Long projectId);
}
