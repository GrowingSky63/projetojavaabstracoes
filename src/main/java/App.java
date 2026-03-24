import application.service.ServiceRegistry;
import interfaces.api.controller.RestServer;
import interfaces.cli.CliApplication;

public class App {
  public static void main(String[] args) throws Exception {
    ServiceRegistry registry = new ServiceRegistry();

    String mode = args.length > 0 ? args[0].toLowerCase() : "api";

    if ("cli".equals(mode)) {
      new CliApplication(
          registry.userService(),
          registry.planService(),
          registry.subscriptionService()).start();
      return;
    }

    int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;
    new RestServer(
        registry.userService(),
        registry.planService(),
        registry.subscriptionService()).start(port);
  }
}
