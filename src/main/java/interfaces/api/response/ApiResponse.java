package interfaces.api.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiResponse {
  public static Map<String, Object> ok(Object data) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("success", true);
    response.put("data", data);
    return response;
  }

  public static Map<String, Object> error(String message) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("success", false);
    response.put("error", message);
    return response;
  }
}
