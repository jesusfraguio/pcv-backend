package es.udc.pcv.backend.model.entities;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectDao extends PagingAndSortingRepository<Project, Long> {
}
