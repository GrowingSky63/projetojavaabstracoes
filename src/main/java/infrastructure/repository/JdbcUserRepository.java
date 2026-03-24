package infrastructure.repository;

import domain.entity.User;
import domain.repository.UserRepository;
import infrastructure.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {
  @Override
  public User save(User user) {
    if (user.getId() == null) {
      return insert(user);
    }
    return update(user);
  }

  @Override
  public Optional<User> findById(Long id) {
    String sql = "SELECT id, first_name, last_name, email FROM users WHERE id = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, id);
      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao buscar usuário por id", ex);
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    String sql = "SELECT id, first_name, last_name, email FROM users WHERE email = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, email);
      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao buscar usuário por email", ex);
    }
  }

  @Override
  public List<User> findAll() {
    String sql = "SELECT id, first_name, last_name, email FROM users ORDER BY id";
    List<User> users = new ArrayList<>();
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery()) {
      while (rs.next()) {
        users.add(mapRow(rs));
      }
      return users;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao listar usuários", ex);
    }
  }

  @Override
  public boolean deleteById(Long id) {
    String sql = "DELETE FROM users WHERE id = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, id);
      return statement.executeUpdate() > 0;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao remover usuário", ex);
    }
  }

  private User insert(User user) {
    String sql = "INSERT INTO users(first_name, last_name, email) VALUES (?, ?, ?)";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, user.getFirstName());
      statement.setString(2, user.getLastName());
      statement.setString(3, user.getEmail());
      statement.executeUpdate();
      try (ResultSet keys = statement.getGeneratedKeys()) {
        if (keys.next()) {
          user.setId(keys.getLong(1));
        }
      }
      return user;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao inserir usuário", ex);
    }
  }

  private User update(User user) {
    String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE id = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, user.getFirstName());
      statement.setString(2, user.getLastName());
      statement.setString(3, user.getEmail());
      statement.setLong(4, user.getId());
      statement.executeUpdate();
      return user;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao atualizar usuário", ex);
    }
  }

  private User mapRow(ResultSet rs) throws SQLException {
    return new User(
        rs.getLong("id"),
        rs.getString("first_name"),
        rs.getString("last_name"),
        rs.getString("email"));
  }
}
