package es.udc.pcv.backend.model.entities;

import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {

  public static Specification<Project> searchProjects(
      String name, String locality, Long collaborationAreaId) {
    return (root, query, builder) -> {

      Predicate predicate = builder.conjunction();

      if (name != null && !name.isEmpty()) {
        predicate = builder.and(predicate, builder.like(root.get("name"), "%" + name + "%"));
      }

      if (locality != null && !locality.isEmpty()) {
        predicate = builder.and(predicate, builder.like(root.get("locality"), "%" + locality + "%"));
      }

      if (collaborationAreaId != null) {
        predicate = builder.and(predicate, builder.equal(root.get("collaborationArea").get("id"), collaborationAreaId));
      }

      return predicate;
    };
  }
}
