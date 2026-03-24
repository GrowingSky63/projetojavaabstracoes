package application.service;

import domain.repository.PlanRepository;
import domain.repository.SubscriptionRepository;
import domain.repository.UserRepository;
import infrastructure.persistence.DatabaseInitializer;
import infrastructure.repository.JdbcPlanRepository;
import infrastructure.repository.JdbcSubscriptionRepository;
import infrastructure.repository.JdbcUserRepository;

public class ServiceRegistry {
  private final UserService userService;
  private final PlanService planService;
  private final SubscriptionService subscriptionService;

  public ServiceRegistry() {
    DatabaseInitializer.initialize();

    UserRepository userRepository = new JdbcUserRepository();
    PlanRepository planRepository = new JdbcPlanRepository();
    SubscriptionRepository subscriptionRepository = new JdbcSubscriptionRepository();

    this.userService = new UserService(userRepository);
    this.planService = new PlanService(planRepository);
    this.subscriptionService = new SubscriptionService(subscriptionRepository, userService, planService);
  }

  public UserService userService() {
    return userService;
  }

  public PlanService planService() {
    return planService;
  }

  public SubscriptionService subscriptionService() {
    return subscriptionService;
  }
}
