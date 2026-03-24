package application.service;

import domain.entity.Plan;
import domain.repository.PlanRepository;
import java.util.List;

public class PlanService {
  private final PlanRepository planRepository;

  public PlanService(PlanRepository planRepository) {
    this.planRepository = planRepository;
  }

  public Plan create(Plan plan) {
    validate(plan);
    planRepository.findByName(plan.getName()).ifPresent(p -> {
      throw new IllegalArgumentException("Plano já cadastrado com esse nome");
    });
    return planRepository.save(plan);
  }

  public Plan update(Long id, Plan plan) {
    validate(plan);
    Plan existing = planRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado"));

    planRepository.findByName(plan.getName()).ifPresent(found -> {
      if (!found.getId().equals(id)) {
        throw new IllegalArgumentException("Plano já cadastrado com esse nome");
      }
    });

    existing.setName(plan.getName());
    existing.setDescription(plan.getDescription());
    existing.setCpus(plan.getCpus());
    existing.setRam(plan.getRam());
    existing.setDisk(plan.getDisk());
    return planRepository.save(existing);
  }

  public Plan getById(Long id) {
    return planRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado"));
  }

  public Plan getByName(String name) {
    return planRepository.findByName(name)
        .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado: " + name));
  }

  public List<Plan> getAll() {
    return planRepository.findAll();
  }

  public boolean delete(Long id) {
    return planRepository.deleteById(id);
  }

  private void validate(Plan plan) {
    if (plan.getName() == null || plan.getName().isBlank()) {
      throw new IllegalArgumentException("name é obrigatório");
    }
    if (plan.getDescription() == null || plan.getDescription().isBlank()) {
      throw new IllegalArgumentException("description é obrigatório");
    }
    if (plan.getCpus() <= 0 || plan.getRam() <= 0 || plan.getDisk() <= 0) {
      throw new IllegalArgumentException("cpus, ram e disk devem ser maiores que zero");
    }
  }
}
