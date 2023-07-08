package es.udc.pcv.backend.model.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RegisteredHoursDao extends PagingAndSortingRepository<RegisteredHours, Long> {
  void deleteAllByParticipation(Participation participation);
  void deleteAllByParticipationVolunteer(Volunteer volunteer);
  Optional<RegisteredHours> findByDateAndParticipation(LocalDate localDate, Participation participation);
  List<RegisteredHours> findByParticipationAndDateBetween(Participation participation, LocalDate startDate, LocalDate endDate);
  List<RegisteredHours> findByParticipationProjectAndDateBetween(Project project, LocalDate startDate, LocalDate endDate);
  List<RegisteredHours> findByParticipationProjectEntityAndDateBetween(Entidad entidad, LocalDate startDate, LocalDate endDate);
}
