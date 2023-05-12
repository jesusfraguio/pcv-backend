package es.udc.pcv.backend.model.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {

  public static Specification<Project> searchProjects(
      String name, String locality, Long collaborationAreaId) {

    return (root, query, builder) -> {

      Predicate predicate = builder.conjunction();

      predicate = builder.and(predicate, builder.equal(root.get("deleted"), false));
      predicate = builder.and(predicate, builder.equal(root.get("visible"), true));

      if (name != null && !name.isEmpty()) {
        String[] tokens = name.split("\\s+");
        List<Predicate> keywordPredicates = new ArrayList<>();
        for (String token : tokens) {
          keywordPredicates.add(builder.like(builder.lower(root.get("name")), "%" + token.toLowerCase() + "%"));
        }
        predicate = builder.and(predicate, builder.or(keywordPredicates.toArray(new Predicate[0])));
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
