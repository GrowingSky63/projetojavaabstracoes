package infrastructure.persistence;

import infrastructure.config.DatabaseConfig;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
  public static void initialize() {
    File dataDir = new File("data");
    if (!dataDir.exists()) {
      dataDir.mkdirs();
    }

    try (Connection connection = DatabaseConfig.getConnection(); Statement statement = connection.createStatement()) {
      statement.execute("""
          CREATE TABLE IF NOT EXISTS users (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              first_name TEXT NOT NULL,
              last_name TEXT NOT NULL,
              email TEXT NOT NULL UNIQUE
          )
          """);

      statement.execute("""
          CREATE TABLE IF NOT EXISTS plans (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL UNIQUE,
              description TEXT NOT NULL,
              cpus INTEGER NOT NULL,
              ram INTEGER NOT NULL,
              disk INTEGER NOT NULL
          )
          """);

      statement.execute("""
          CREATE TABLE IF NOT EXISTS subscriptions (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              subscription_id TEXT NOT NULL UNIQUE,
              user_id INTEGER NOT NULL,
              plan_id INTEGER NOT NULL,
              payment_method TEXT NOT NULL,
              payment_approve_date TEXT,
              start_date TEXT,
              end_date TEXT,
              status TEXT,
              FOREIGN KEY (user_id) REFERENCES users(id),
              FOREIGN KEY (plan_id) REFERENCES plans(id)
          )
          """);
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao inicializar banco de dados", ex);
    }
  }
}
