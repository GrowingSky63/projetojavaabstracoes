package interfaces.api.controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
  public static String readBody(HttpExchange exchange) throws IOException {
    try (InputStream input = exchange.getRequestBody()) {
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  public static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(statusCode, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.close();
  }

  public static Long parseId(String value) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("ID inválido: " + value);
    }
  }
}
