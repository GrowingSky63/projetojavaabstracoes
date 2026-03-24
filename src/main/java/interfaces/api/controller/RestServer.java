package interfaces.api.controller;

import application.dtos.ActivationResultDto;
import application.dtos.CheckoutActivationRequestDto;
import application.service.PlanService;
import application.service.SubscriptionService;
import application.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import domain.entity.Plan;
import domain.entity.Subscription;
import domain.entity.User;
import interfaces.api.response.ApiResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class RestServer {
  private final UserService userService;
  private final PlanService planService;
  private final SubscriptionService subscriptionService;

  public RestServer(UserService userService, PlanService planService, SubscriptionService subscriptionService) {
    this.userService = userService;
    this.planService = planService;
    this.subscriptionService = subscriptionService;
  }

  public void start(int port) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/", this::handle);
    server.setExecutor(Executors.newFixedThreadPool(8));
    server.start();
    System.out.println("REST API rodando em http://localhost:" + port);
  }

  private void handle(HttpExchange exchange) throws IOException {
    try {
      String method = exchange.getRequestMethod();
      String path = exchange.getRequestURI().getPath();

      if ("GET".equals(method) && "/health".equals(path)) {
        respond(exchange, 200, ApiResponse.ok(Map.of("status", "UP")));
        return;
      }

      if ("GET".equals(method) && "/openapi.yaml".equals(path)) {
        handleOpenApi(exchange);
        return;
      }

      if ("GET".equals(method) && "/docs".equals(path)) {
        handleDocs(exchange);
        return;
      }

      if (path.equals("/users") || path.startsWith("/users/")) {
        handleUsers(exchange, method, path);
        return;
      }

      if (path.equals("/plans") || path.startsWith("/plans/")) {
        handlePlans(exchange, method, path);
        return;
      }

      if (path.equals("/subscriptions") || path.startsWith("/subscriptions/")) {
        handleSubscriptions(exchange, method, path);
        return;
      }

      if ("POST".equals(method) && "/checkout/activate".equals(path)) {
        handleCheckoutActivate(exchange);
        return;
      }

      respond(exchange, 404, ApiResponse.error("Rota não encontrada"));
    } catch (IllegalArgumentException ex) {
      respond(exchange, 400, ApiResponse.error(ex.getMessage()));
    } catch (Exception ex) {
      respond(exchange, 500, ApiResponse.error("Erro interno: " + ex.getMessage()));
    }
  }

  private void handleUsers(HttpExchange exchange, String method, String path) throws IOException {
    if ("GET".equals(method) && "/users".equals(path)) {
      List<Map<String, Object>> data = userService.getAll().stream().map(this::toUserMap).toList();
      respond(exchange, 200, ApiResponse.ok(data));
      return;
    }

    if ("POST".equals(method) && "/users".equals(path)) {
      Map<String, String> body = JsonUtil.parseObject(HttpUtils.readBody(exchange));
      User created = userService.create(new User(
          null,
          body.get("first_name"),
          body.get("last_name"),
          body.get("email")));
      respond(exchange, 201, ApiResponse.ok(toUserMap(created)));
      return;
    }

    if (path.startsWith("/users/")) {
      Long id = HttpUtils.parseId(path.substring("/users/".length()));

      if ("GET".equals(method)) {
        respond(exchange, 200, ApiResponse.ok(toUserMap(userService.getById(id))));
        return;
      }

      if ("PUT".equals(method)) {
        Map<String, String> body = JsonUtil.parseObject(HttpUtils.readBody(exchange));
        User updated = userService.update(id, new User(
            id,
            body.get("first_name"),
            body.get("last_name"),
            body.get("email")));
        respond(exchange, 200, ApiResponse.ok(toUserMap(updated)));
        return;
      }

      if ("DELETE".equals(method)) {
        boolean deleted = userService.delete(id);
        if (!deleted) {
          throw new IllegalArgumentException("Usuário não encontrado");
        }
        respond(exchange, 200, ApiResponse.ok(Map.of("deleted", true)));
        return;
      }
    }

    respond(exchange, 405, ApiResponse.error("Método não permitido"));
  }

  private void handlePlans(HttpExchange exchange, String method, String path) throws IOException {
    if ("GET".equals(method) && "/plans".equals(path)) {
      List<Map<String, Object>> data = planService.getAll().stream().map(this::toPlanMap).toList();
      respond(exchange, 200, ApiResponse.ok(data));
      return;
    }

    if ("POST".equals(method) && "/plans".equals(path)) {
      Map<String, String> body = JsonUtil.parseObject(HttpUtils.readBody(exchange));
      Plan created = planService.create(new Plan(
          null,
          body.get("name"),
          body.get("description"),
          Integer.parseInt(body.getOrDefault("cpus", "0")),
          Integer.parseInt(body.getOrDefault("ram", "0")),
          Integer.parseInt(body.getOrDefault("disk", "0"))));
      respond(exchange, 201, ApiResponse.ok(toPlanMap(created)));
      return;
    }

    if (path.startsWith("/plans/")) {
      Long id = HttpUtils.parseId(path.substring("/plans/".length()));

      if ("GET".equals(method)) {
        respond(exchange, 200, ApiResponse.ok(toPlanMap(planService.getById(id))));
        return;
      }

      if ("PUT".equals(method)) {
        Map<String, String> body = JsonUtil.parseObject(HttpUtils.readBody(exchange));
        Plan updated = planService.update(id, new Plan(
            id,
            body.get("name"),
            body.get("description"),
            Integer.parseInt(body.getOrDefault("cpus", "0")),
            Integer.parseInt(body.getOrDefault("ram", "0")),
            Integer.parseInt(body.getOrDefault("disk", "0"))));
        respond(exchange, 200, ApiResponse.ok(toPlanMap(updated)));
        return;
      }

      if ("DELETE".equals(method)) {
        boolean deleted = planService.delete(id);
        if (!deleted) {
          throw new IllegalArgumentException("Plano não encontrado");
        }
        respond(exchange, 200, ApiResponse.ok(Map.of("deleted", true)));
        return;
      }
    }

    respond(exchange, 405, ApiResponse.error("Método não permitido"));
  }

  private void handleSubscriptions(HttpExchange exchange, String method, String path) throws IOException {
    if ("GET".equals(method) && "/subscriptions".equals(path)) {
      List<Map<String, Object>> data = subscriptionService.getAll().stream().map(this::toSubscriptionMap).toList();
      respond(exchange, 200, ApiResponse.ok(data));
      return;
    }

    if ("POST".equals(method) && "/subscriptions".equals(path)) {
      Map<String, String> body = JsonUtil.parseObject(HttpUtils.readBody(exchange));
      Subscription created = subscriptionService.create(new Subscription(
          null,
          body.get("subscription_id"),
          Long.parseLong(body.getOrDefault("user_id", "0")),
          Long.parseLong(body.getOrDefault("plan_id", "0")),
          body.get("payment_method"),
          parseDate(body.get("payment_approve_date")),
          parseDate(body.get("start_date")),
          parseDate(body.get("end_date")),
          body.getOrDefault("status", "ACTIVE")));
      respond(exchange, 201, ApiResponse.ok(toSubscriptionMap(created)));
      return;
    }

    if (path.startsWith("/subscriptions/")) {
      Long id = HttpUtils.parseId(path.substring("/subscriptions/".length()));

      if ("GET".equals(method)) {
        respond(exchange, 200, ApiResponse.ok(toSubscriptionMap(subscriptionService.getById(id))));
        return;
      }

      if ("PUT".equals(method)) {
        Map<String, String> body = JsonUtil.parseObject(HttpUtils.readBody(exchange));
        Subscription updated = subscriptionService.update(id, new Subscription(
            id,
            body.get("subscription_id"),
            Long.parseLong(body.getOrDefault("user_id", "0")),
            Long.parseLong(body.getOrDefault("plan_id", "0")),
            body.get("payment_method"),
            parseDate(body.get("payment_approve_date")),
            parseDate(body.get("start_date")),
            parseDate(body.get("end_date")),
            body.getOrDefault("status", "ACTIVE")));
        respond(exchange, 200, ApiResponse.ok(toSubscriptionMap(updated)));
        return;
      }

      if ("DELETE".equals(method)) {
        boolean deleted = subscriptionService.delete(id);
        if (!deleted) {
          throw new IllegalArgumentException("Assinatura não encontrada");
        }
        respond(exchange, 200, ApiResponse.ok(Map.of("deleted", true)));
        return;
      }
    }

    respond(exchange, 405, ApiResponse.error("Método não permitido"));
  }

  private void handleCheckoutActivate(HttpExchange exchange) throws IOException {
    Map<String, String> body = JsonUtil.parseObject(HttpUtils.readBody(exchange));

    CheckoutActivationRequestDto request = new CheckoutActivationRequestDto();
    request.setFirstName(body.get("first_name"));
    request.setLastName(body.get("last_name"));
    request.setEmail(body.get("email"));
    request.setPlan(body.get("plan"));
    request.setSubscriptionId(body.get("subscription_id"));
    request.setPaymentMethod(body.get("payment_method"));
    request.setPaymentAproveDate(body.get("payment_aprove_date"));

    ActivationResultDto result = subscriptionService.activateFromCheckout(request);

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("action", result.getAction());
    payload.put("user_id", result.getUserId());
    payload.put("subscription_id", result.getSubscriptionId());
    payload.put("message", result.getMessage());

    respond(exchange, 200, ApiResponse.ok(payload));
  }

  private void handleOpenApi(HttpExchange exchange) throws IOException {
    Path file = Path.of("docs", "openapi.yaml");
    if (!Files.exists(file)) {
      respond(exchange, 404, ApiResponse.error("Arquivo de especificação OpenAPI não encontrado"));
      return;
    }

    byte[] bytes = Files.readAllBytes(file);
    sendRaw(exchange, 200, "application/yaml; charset=utf-8", bytes);
  }

  private void handleDocs(HttpExchange exchange) throws IOException {
    Path file = Path.of("docs", "swagger.html");
    if (!Files.exists(file)) {
      respond(exchange, 404, ApiResponse.error("Arquivo Swagger UI não encontrado"));
      return;
    }

    byte[] bytes = Files.readString(file, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
    sendRaw(exchange, 200, "text/html; charset=utf-8", bytes);
  }

  private LocalDateTime parseDate(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return LocalDateTime.parse(value);
  }

  private void respond(HttpExchange exchange, int statusCode, Map<String, Object> payload) throws IOException {
    HttpUtils.sendJson(exchange, statusCode, JsonUtil.toJson(payload));
  }

  private void sendRaw(HttpExchange exchange, int statusCode, String contentType, byte[] body) throws IOException {
    exchange.getResponseHeaders().set("Content-Type", contentType);
    exchange.sendResponseHeaders(statusCode, body.length);
    exchange.getResponseBody().write(body);
    exchange.close();
  }

  private Map<String, Object> toUserMap(User user) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", user.getId());
    map.put("first_name", user.getFirstName());
    map.put("last_name", user.getLastName());
    map.put("email", user.getEmail());
    return map;
  }

  private Map<String, Object> toPlanMap(Plan plan) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", plan.getId());
    map.put("name", plan.getName());
    map.put("description", plan.getDescription());
    map.put("cpus", plan.getCpus());
    map.put("ram", plan.getRam());
    map.put("disk", plan.getDisk());
    return map;
  }

  private Map<String, Object> toSubscriptionMap(Subscription subscription) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", subscription.getId());
    map.put("subscription_id", subscription.getSubscriptionId());
    map.put("user_id", subscription.getUserId());
    map.put("plan_id", subscription.getPlanId());
    map.put("payment_method", subscription.getPaymentMethod());
    map.put("payment_approve_date",
        subscription.getPaymentApproveDate() == null ? null : subscription.getPaymentApproveDate().toString());
    map.put("start_date", subscription.getStartDate() == null ? null : subscription.getStartDate().toString());
    map.put("end_date", subscription.getEndDate() == null ? null : subscription.getEndDate().toString());
    map.put("status", subscription.getStatus());
    return map;
  }
}
