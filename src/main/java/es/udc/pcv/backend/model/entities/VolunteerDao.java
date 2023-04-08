package es.udc.pcv.backend.model.entities;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VolunteerDao extends PagingAndSortingRepository<Volunteer, Long> {
  boolean existsByDni(String dni);
  Optional<Volunteer> findByDni(String dni);
  Optional<Volunteer> findByUserId(Long userId);
}
