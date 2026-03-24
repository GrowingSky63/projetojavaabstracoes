package infrastructure.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
  public static Connection getConnection() throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      throw new SQLException("Driver SQLite não encontrado no classpath.", e);
    }
    return DriverManager.getConnection("jdbc:sqlite:./data/plans.db");
  }
}
