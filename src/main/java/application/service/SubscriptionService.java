package application.service;

import domain.entity.Plan;
import domain.entity.Subscription;
import domain.entity.User;
import domain.repository.SubscriptionRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import application.dtos.ActivationResultDto;
import application.dtos.CheckoutActivationRequestDto;

public class SubscriptionService {
  private final SubscriptionRepository subscriptionRepository;
  private final UserService userService;
  private final PlanService planService;

  public SubscriptionService(
      SubscriptionRepository subscriptionRepository,
      UserService userService,
      PlanService planService) {
    this.subscriptionRepository = subscriptionRepository;
    this.userService = userService;
    this.planService = planService;
  }

  public Subscription create(Subscription subscription) {
    validate(subscription);
    subscriptionRepository.findBySubscriptionId(subscription.getSubscriptionId()).ifPresent(s -> {
      throw new IllegalArgumentException("subscription_id já existe");
    });
    return subscriptionRepository.save(subscription);
  }

  public Subscription update(Long id, Subscription subscription) {
    validate(subscription);
    Subscription existing = subscriptionRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Assinatura não encontrada"));

    subscriptionRepository.findBySubscriptionId(subscription.getSubscriptionId()).ifPresent(found -> {
      if (!found.getId().equals(id)) {
        throw new IllegalArgumentException("subscription_id já existe");
      }
    });

    existing.setSubscriptionId(subscription.getSubscriptionId());
    existing.setUserId(subscription.getUserId());
    existing.setPlanId(subscription.getPlanId());
    existing.setPaymentMethod(subscription.getPaymentMethod());
    existing.setPaymentApproveDate(subscription.getPaymentApproveDate());
    existing.setStartDate(subscription.getStartDate());
    existing.setEndDate(subscription.getEndDate());
    existing.setStatus(subscription.getStatus());

    return subscriptionRepository.save(existing);
  }

  public Subscription getById(Long id) {
    return subscriptionRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Assinatura não encontrada"));
  }

  public List<Subscription> getAll() {
    return subscriptionRepository.findAll();
  }

  public boolean delete(Long id) {
    return subscriptionRepository.deleteById(id);
  }

  public ActivationResultDto activateFromCheckout(CheckoutActivationRequestDto request) {
    validateCheckout(request);

    User user = userService.findOrCreateByEmail(request.getFirstName(), request.getLastName(), request.getEmail());
    Plan plan = planService.getByName(request.getPlan());
    LocalDateTime approveDate = parseDate(request.getPaymentAproveDate());

    Subscription existing = subscriptionRepository.findBySubscriptionId(request.getSubscriptionId()).orElse(null);

    if (existing == null) {
      Subscription created = new Subscription(
          null,
          request.getSubscriptionId(),
          user.getId(),
          plan.getId(),
          request.getPaymentMethod(),
          approveDate,
          approveDate,
          approveDate.plusDays(30),
          "ACTIVE");
      Subscription saved = subscriptionRepository.save(created);
      return new ActivationResultDto("CREATED", user.getId(), saved.getId(), "Nova assinatura criada");
    }

    if (!existing.getUserId().equals(user.getId())) {
      throw new IllegalArgumentException("subscription_id já pertence a outro usuário");
    }

    LocalDateTime nowBase = existing.getEndDate() != null && existing.getEndDate().isAfter(approveDate)
        ? existing.getEndDate()
        : approveDate;

    existing.setPlanId(plan.getId());
    existing.setPaymentMethod(request.getPaymentMethod());
    existing.setPaymentApproveDate(approveDate);
    existing.setStartDate(approveDate);
    existing.setEndDate(nowBase.plusDays(30));
    existing.setStatus("ACTIVE");
    Subscription renewed = subscriptionRepository.save(existing);

    return new ActivationResultDto("RENEWED", user.getId(), renewed.getId(), "Assinatura renovada");
  }

  private LocalDateTime parseDate(String value) {
    if (value == null || value.isBlank()) {
      return LocalDateTime.now();
    }
    try {
      return LocalDateTime.parse(value);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("payment_aprove_date deve estar no formato ISO-8601, ex: 2026-03-24T10:30:00");
    }
  }

  private void validateCheckout(CheckoutActivationRequestDto request) {
    if (request.getFirstName() == null || request.getFirstName().isBlank()) {
      throw new IllegalArgumentException("first_name é obrigatório");
    }
    if (request.getLastName() == null || request.getLastName().isBlank()) {
      throw new IllegalArgumentException("last_name é obrigatório");
    }
    if (request.getEmail() == null || request.getEmail().isBlank()) {
      throw new IllegalArgumentException("email é obrigatório");
    }
    if (request.getPlan() == null || request.getPlan().isBlank()) {
      throw new IllegalArgumentException("plan é obrigatório");
    }
    if (request.getSubscriptionId() == null || request.getSubscriptionId().isBlank()) {
      throw new IllegalArgumentException("subscription_id é obrigatório");
    }
    if (request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()) {
      throw new IllegalArgumentException("payment_method é obrigatório");
    }
  }

  private void validate(Subscription subscription) {
    if (subscription.getSubscriptionId() == null || subscription.getSubscriptionId().isBlank()) {
      throw new IllegalArgumentException("subscription_id é obrigatório");
    }
    if (subscription.getUserId() == null) {
      throw new IllegalArgumentException("user_id é obrigatório");
    }
    if (subscription.getPlanId() == null) {
      throw new IllegalArgumentException("plan_id é obrigatório");
    }
    if (subscription.getPaymentMethod() == null || subscription.getPaymentMethod().isBlank()) {
      throw new IllegalArgumentException("payment_method é obrigatório");
    }
  }
}
