package infrastructure.repository;

import domain.entity.Subscription;
import domain.repository.SubscriptionRepository;
import infrastructure.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcSubscriptionRepository implements SubscriptionRepository {
  @Override
  public Subscription save(Subscription subscription) {
    if (subscription.getId() == null) {
      return insert(subscription);
    }
    return update(subscription);
  }

  @Override
  public Optional<Subscription> findById(Long id) {
    String sql = "SELECT * FROM subscriptions WHERE id = ?";
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
      throw new RuntimeException("Erro ao buscar assinatura por id", ex);
    }
  }

  @Override
  public Optional<Subscription> findBySubscriptionId(String subscriptionId) {
    String sql = "SELECT * FROM subscriptions WHERE subscription_id = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, subscriptionId);
      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao buscar assinatura por subscription_id", ex);
    }
  }

  @Override
  public List<Subscription> findByUserId(Long userId) {
    String sql = "SELECT * FROM subscriptions WHERE user_id = ? ORDER BY id";
    List<Subscription> subscriptions = new ArrayList<>();
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, userId);
      try (ResultSet rs = statement.executeQuery()) {
        while (rs.next()) {
          subscriptions.add(mapRow(rs));
        }
      }
      return subscriptions;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao listar assinaturas do usuário", ex);
    }
  }

  @Override
  public List<Subscription> findAll() {
    String sql = "SELECT * FROM subscriptions ORDER BY id";
    List<Subscription> subscriptions = new ArrayList<>();
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery()) {
      while (rs.next()) {
        subscriptions.add(mapRow(rs));
      }
      return subscriptions;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao listar assinaturas", ex);
    }
  }

  @Override
  public boolean deleteById(Long id) {
    String sql = "DELETE FROM subscriptions WHERE id = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, id);
      return statement.executeUpdate() > 0;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao remover assinatura", ex);
    }
  }

  private Subscription insert(Subscription subscription) {
    String sql = """
        INSERT INTO subscriptions(
            subscription_id, user_id, plan_id, payment_method,
            payment_approve_date, start_date, end_date, status
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      bind(statement, subscription);
      statement.executeUpdate();
      try (ResultSet keys = statement.getGeneratedKeys()) {
        if (keys.next()) {
          subscription.setId(keys.getLong(1));
        }
      }
      return subscription;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao inserir assinatura", ex);
    }
  }

  private Subscription update(Subscription subscription) {
    String sql = """
        UPDATE subscriptions SET
            subscription_id = ?,
            user_id = ?,
            plan_id = ?,
            payment_method = ?,
            payment_approve_date = ?,
            start_date = ?,
            end_date = ?,
            status = ?
        WHERE id = ?
        """;

    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      bind(statement, subscription);
      statement.setLong(9, subscription.getId());
      statement.executeUpdate();
      return subscription;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao atualizar assinatura", ex);
    }
  }

  private void bind(PreparedStatement statement, Subscription s) throws SQLException {
    statement.setString(1, s.getSubscriptionId());
    statement.setLong(2, s.getUserId());
    statement.setLong(3, s.getPlanId());
    statement.setString(4, s.getPaymentMethod());
    statement.setString(5, asString(s.getPaymentApproveDate()));
    statement.setString(6, asString(s.getStartDate()));
    statement.setString(7, asString(s.getEndDate()));
    statement.setString(8, s.getStatus());
  }

  private Subscription mapRow(ResultSet rs) throws SQLException {
    return new Subscription(
        rs.getLong("id"),
        rs.getString("subscription_id"),
        rs.getLong("user_id"),
        rs.getLong("plan_id"),
        rs.getString("payment_method"),
        parseDate(rs.getString("payment_approve_date")),
        parseDate(rs.getString("start_date")),
        parseDate(rs.getString("end_date")),
        rs.getString("status"));
  }

  private String asString(LocalDateTime value) {
    return value == null ? null : value.toString();
  }

  private LocalDateTime parseDate(String value) {
    return value == null || value.isBlank() ? null : LocalDateTime.parse(value);
  }
}
