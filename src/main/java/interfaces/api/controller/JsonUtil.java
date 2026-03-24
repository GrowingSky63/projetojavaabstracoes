package interfaces.api.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
  public static Map<String, String> parseObject(String json) {
    Map<String, String> result = new LinkedHashMap<>();
    if (json == null) {
      return result;
    }

    String trimmed = json.trim();
    if (trimmed.startsWith("{")) {
      trimmed = trimmed.substring(1);
    }
    if (trimmed.endsWith("}")) {
      trimmed = trimmed.substring(0, trimmed.length() - 1);
    }

    List<String> tokens = splitTopLevel(trimmed);
    for (String token : tokens) {
      int idx = token.indexOf(':');
      if (idx <= 0) {
        continue;
      }
      String key = unquote(token.substring(0, idx).trim());
      String rawValue = token.substring(idx + 1).trim();
      String value = unquote(rawValue);
      result.put(key, value);
    }
    return result;
  }

  public static String toJson(Object value) {
    if (value == null) {
      return "null";
    }
    if (value instanceof String s) {
      return quote(s);
    }
    if (value instanceof Number || value instanceof Boolean) {
      return String.valueOf(value);
    }
    if (value instanceof Map<?, ?> map) {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      boolean first = true;
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        if (!first) {
          sb.append(",");
        }
        first = false;
        sb.append(quote(String.valueOf(entry.getKey())));
        sb.append(":");
        sb.append(toJson(entry.getValue()));
      }
      sb.append("}");
      return sb.toString();
    }
    if (value instanceof List<?> list) {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      for (int i = 0; i < list.size(); i++) {
        if (i > 0) {
          sb.append(",");
        }
        sb.append(toJson(list.get(i)));
      }
      sb.append("]");
      return sb.toString();
    }
    return quote(String.valueOf(value));
  }

  private static List<String> splitTopLevel(String content) {
    List<String> parts = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;
    boolean escaped = false;

    for (int i = 0; i < content.length(); i++) {
      char c = content.charAt(i);
      if (escaped) {
        current.append(c);
        escaped = false;
        continue;
      }
      if (c == '\\') {
        current.append(c);
        escaped = true;
        continue;
      }
      if (c == '"') {
        inQuotes = !inQuotes;
        current.append(c);
        continue;
      }
      if (c == ',' && !inQuotes) {
        parts.add(current.toString().trim());
        current.setLength(0);
        continue;
      }
      current.append(c);
    }

    if (!current.isEmpty()) {
      parts.add(current.toString().trim());
    }

    return parts;
  }

  private static String unquote(String value) {
    String trimmed = value.trim();
    if (trimmed.equals("null")) {
      return null;
    }
    if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
      String inner = trimmed.substring(1, trimmed.length() - 1);
      return inner
          .replace("\\\"", "\"")
          .replace("\\\\", "\\")
          .replace("\\n", "\n")
          .replace("\\r", "\r")
          .replace("\\t", "\t");
    }
    return trimmed;
  }

  private static String quote(String value) {
    return "\"" + value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t") + "\"";
  }
}
