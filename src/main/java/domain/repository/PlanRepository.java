package domain.repository;

import domain.entity.Plan;
import java.util.List;
import java.util.Optional;

public interface PlanRepository {
  Plan save(Plan plan);

  Optional<Plan> findById(Long id);

  Optional<Plan> findByName(String name);

  List<Plan> findAll();

  boolean deleteById(Long id);
}
