package interfaces.cli;

import application.dtos.CheckoutActivationRequestDto;
import application.service.PlanService;
import application.service.SubscriptionService;
import application.service.UserService;
import domain.entity.Plan;
import domain.entity.Subscription;
import domain.entity.User;
import java.time.LocalDateTime;
import java.util.Scanner;

public class CliApplication {
  private final UserService userService;
  private final PlanService planService;
  private final SubscriptionService subscriptionService;

  public CliApplication(UserService userService, PlanService planService, SubscriptionService subscriptionService) {
    this.userService = userService;
    this.planService = planService;
    this.subscriptionService = subscriptionService;
  }

  public void start() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      try {
        System.out.println("\n=== Sistema de Gestão de Planos (CLI) ===");
        System.out.println("1 - CRUD Usuários");
        System.out.println("2 - CRUD Planos");
        System.out.println("3 - CRUD Assinaturas");
        System.out.println("4 - Ativar/Renovar via Checkout");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");

        String option = scanner.nextLine();
        switch (option) {
          case "1" -> usersMenu(scanner);
          case "2" -> plansMenu(scanner);
          case "3" -> subscriptionsMenu(scanner);
          case "4" -> activateCheckout(scanner);
          case "0" -> {
            System.out.println("Encerrando CLI...");
            return;
          }
          default -> System.out.println("Opção inválida");
        }
      } catch (Exception ex) {
        System.out.println("Erro: " + ex.getMessage());
      }
    }
  }

  private void usersMenu(Scanner scanner) {
    System.out.println("\n-- Usuários --");
    System.out.println("1 - Criar");
    System.out.println("2 - Listar");
    System.out.println("3 - Buscar por ID");
    System.out.println("4 - Atualizar");
    System.out.println("5 - Remover");
    System.out.print("Escolha: ");
    String option = scanner.nextLine();

    switch (option) {
      case "1" -> {
        User created = userService
            .create(new User(null, ask(scanner, "First name"), ask(scanner, "Last name"), ask(scanner, "Email")));
        System.out.println("Criado: ID=" + created.getId());
      }
      case "2" -> userService.getAll().forEach(u -> System.out
          .println(u.getId() + " - " + u.getFirstName() + " " + u.getLastName() + " <" + u.getEmail() + ">"));
      case "3" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        User user = userService.getById(id);
        System.out.println(
            user.getId() + " - " + user.getFirstName() + " " + user.getLastName() + " <" + user.getEmail() + ">");
      }
      case "4" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        User updated = userService.update(id,
            new User(id, ask(scanner, "First name"), ask(scanner, "Last name"), ask(scanner, "Email")));
        System.out.println("Atualizado: " + updated.getId());
      }
      case "5" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        System.out.println(userService.delete(id) ? "Removido" : "Não encontrado");
      }
      default -> System.out.println("Opção inválida");
    }
  }

  private void plansMenu(Scanner scanner) {
    System.out.println("\n-- Planos --");
    System.out.println("1 - Criar");
    System.out.println("2 - Listar");
    System.out.println("3 - Buscar por ID");
    System.out.println("4 - Atualizar");
    System.out.println("5 - Remover");
    System.out.print("Escolha: ");
    String option = scanner.nextLine();

    switch (option) {
      case "1" -> {
        Plan created = planService.create(new Plan(
            null,
            ask(scanner, "Name"),
            ask(scanner, "Description"),
            Integer.parseInt(ask(scanner, "CPUs")),
            Integer.parseInt(ask(scanner, "RAM")),
            Integer.parseInt(ask(scanner, "Disk"))));
        System.out.println("Criado: ID=" + created.getId());
      }
      case "2" -> planService.getAll().forEach(p -> System.out.println(p.getId() + " - " + p.getName() + " (CPU="
          + p.getCpus() + ", RAM=" + p.getRam() + ", DISK=" + p.getDisk() + ")"));
      case "3" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        Plan plan = planService.getById(id);
        System.out.println(plan.getId() + " - " + plan.getName() + " - " + plan.getDescription());
      }
      case "4" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        Plan updated = planService.update(id, new Plan(
            id,
            ask(scanner, "Name"),
            ask(scanner, "Description"),
            Integer.parseInt(ask(scanner, "CPUs")),
            Integer.parseInt(ask(scanner, "RAM")),
            Integer.parseInt(ask(scanner, "Disk"))));
        System.out.println("Atualizado: " + updated.getId());
      }
      case "5" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        System.out.println(planService.delete(id) ? "Removido" : "Não encontrado");
      }
      default -> System.out.println("Opção inválida");
    }
  }

  private void subscriptionsMenu(Scanner scanner) {
    System.out.println("\n-- Assinaturas --");
    System.out.println("1 - Criar");
    System.out.println("2 - Listar");
    System.out.println("3 - Buscar por ID");
    System.out.println("4 - Atualizar");
    System.out.println("5 - Remover");
    System.out.print("Escolha: ");
    String option = scanner.nextLine();

    switch (option) {
      case "1" -> {
        Subscription created = subscriptionService.create(new Subscription(
            null,
            ask(scanner, "Subscription ID"),
            Long.parseLong(ask(scanner, "User ID")),
            Long.parseLong(ask(scanner, "Plan ID")),
            ask(scanner, "Payment method"),
            parseOptionalDate(ask(scanner, "Payment approve date (ISO)")),
            parseOptionalDate(ask(scanner, "Start date (ISO)")),
            parseOptionalDate(ask(scanner, "End date (ISO)")),
            ask(scanner, "Status")));
        System.out.println("Criada: ID=" + created.getId());
      }
      case "2" -> subscriptionService.getAll().forEach(s -> System.out.println(s.getId() + " - sub="
          + s.getSubscriptionId() + " user=" + s.getUserId() + " plan=" + s.getPlanId() + " end=" + s.getEndDate()));
      case "3" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        Subscription s = subscriptionService.getById(id);
        System.out.println(s.getId() + " - sub=" + s.getSubscriptionId() + " user=" + s.getUserId() + " plan="
            + s.getPlanId() + " status=" + s.getStatus());
      }
      case "4" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        Subscription updated = subscriptionService.update(id, new Subscription(
            id,
            ask(scanner, "Subscription ID"),
            Long.parseLong(ask(scanner, "User ID")),
            Long.parseLong(ask(scanner, "Plan ID")),
            ask(scanner, "Payment method"),
            parseOptionalDate(ask(scanner, "Payment approve date (ISO)")),
            parseOptionalDate(ask(scanner, "Start date (ISO)")),
            parseOptionalDate(ask(scanner, "End date (ISO)")),
            ask(scanner, "Status")));
        System.out.println("Atualizada: " + updated.getId());
      }
      case "5" -> {
        Long id = Long.parseLong(ask(scanner, "ID"));
        System.out.println(subscriptionService.delete(id) ? "Removida" : "Não encontrada");
      }
      default -> System.out.println("Opção inválida");
    }
  }

  private void activateCheckout(Scanner scanner) {
    CheckoutActivationRequestDto request = new CheckoutActivationRequestDto();
    request.setFirstName(ask(scanner, "first_name"));
    request.setLastName(ask(scanner, "last_name"));
    request.setEmail(ask(scanner, "email"));
    request.setPlan(ask(scanner, "plan"));
    request.setSubscriptionId(ask(scanner, "subscription_id"));
    request.setPaymentMethod(ask(scanner, "payment_method"));
    request.setPaymentAproveDate(ask(scanner, "payment_aprove_date (ISO)"));

    var result = subscriptionService.activateFromCheckout(request);
    System.out.println("Ação: " + result.getAction() + " | user_id=" + result.getUserId() + " | subscription_id="
        + result.getSubscriptionId());
  }

  private LocalDateTime parseOptionalDate(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return LocalDateTime.parse(value);
  }

  private String ask(Scanner scanner, String label) {
    System.out.print(label + ": ");
    return scanner.nextLine();
  }
}
