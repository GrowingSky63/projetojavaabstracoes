package infrastructure.repository;

import domain.entity.Plan;
import domain.repository.PlanRepository;
import infrastructure.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPlanRepository implements PlanRepository {
  @Override
  public Plan save(Plan plan) {
    if (plan.getId() == null) {
      return insert(plan);
    }
    return update(plan);
  }

  @Override
  public Optional<Plan> findById(Long id) {
    String sql = "SELECT id, name, description, cpus, ram, disk FROM plans WHERE id = ?";
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
      throw new RuntimeException("Erro ao buscar plano por id", ex);
    }
  }

  @Override
  public Optional<Plan> findByName(String name) {
    String sql = "SELECT id, name, description, cpus, ram, disk FROM plans WHERE name = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, name);
      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao buscar plano por nome", ex);
    }
  }

  @Override
  public List<Plan> findAll() {
    String sql = "SELECT id, name, description, cpus, ram, disk FROM plans ORDER BY id";
    List<Plan> plans = new ArrayList<>();
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery()) {
      while (rs.next()) {
        plans.add(mapRow(rs));
      }
      return plans;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao listar planos", ex);
    }
  }

  @Override
  public boolean deleteById(Long id) {
    String sql = "DELETE FROM plans WHERE id = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, id);
      return statement.executeUpdate() > 0;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao remover plano", ex);
    }
  }

  private Plan insert(Plan plan) {
    String sql = "INSERT INTO plans(name, description, cpus, ram, disk) VALUES (?, ?, ?, ?, ?)";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, plan.getName());
      statement.setString(2, plan.getDescription());
      statement.setInt(3, plan.getCpus());
      statement.setInt(4, plan.getRam());
      statement.setInt(5, plan.getDisk());
      statement.executeUpdate();
      try (ResultSet keys = statement.getGeneratedKeys()) {
        if (keys.next()) {
          plan.setId(keys.getLong(1));
        }
      }
      return plan;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao inserir plano", ex);
    }
  }

  private Plan update(Plan plan) {
    String sql = "UPDATE plans SET name = ?, description = ?, cpus = ?, ram = ?, disk = ? WHERE id = ?";
    try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, plan.getName());
      statement.setString(2, plan.getDescription());
      statement.setInt(3, plan.getCpus());
      statement.setInt(4, plan.getRam());
      statement.setInt(5, plan.getDisk());
      statement.setLong(6, plan.getId());
      statement.executeUpdate();
      return plan;
    } catch (SQLException ex) {
      throw new RuntimeException("Erro ao atualizar plano", ex);
    }
  }

  private Plan mapRow(ResultSet rs) throws SQLException {
    return new Plan(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("description"),
        rs.getInt("cpus"),
        rs.getInt("ram"),
        rs.getInt("disk"));
  }
}
