package es.udc.pcv.backend.model.entities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectDao extends PagingAndSortingRepository<Project, Long>,
    JpaSpecificationExecutor<Project> {
  Page<Project> findAll(Specification<Project> spec, Pageable pageable);
  Page<Project> findAllByEntityId(Long entityId, Pageable pageable);
}
