package application.service;

import domain.entity.User;
import domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;

public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User create(User user) {
    validate(user);
    Optional<User> existing = userRepository.findByEmail(user.getEmail());
    if (existing.isPresent()) {
      throw new IllegalArgumentException("Email já cadastrado");
    }
    return userRepository.save(user);
  }

  public User update(Long id, User user) {
    validate(user);
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    Optional<User> emailOwner = userRepository.findByEmail(user.getEmail());
    if (emailOwner.isPresent() && !emailOwner.get().getId().equals(id)) {
      throw new IllegalArgumentException("Email já cadastrado");
    }

    existing.setFirstName(user.getFirstName());
    existing.setLastName(user.getLastName());
    existing.setEmail(user.getEmail());
    return userRepository.save(existing);
  }

  public User getById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
  }

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public boolean delete(Long id) {
    return userRepository.deleteById(id);
  }

  public User findOrCreateByEmail(String firstName, String lastName, String email) {
    return userRepository.findByEmail(email)
        .orElseGet(() -> userRepository.save(new User(null, firstName, lastName, email)));
  }

  private void validate(User user) {
    if (user.getFirstName() == null || user.getFirstName().isBlank()) {
      throw new IllegalArgumentException("first_name é obrigatório");
    }
    if (user.getLastName() == null || user.getLastName().isBlank()) {
      throw new IllegalArgumentException("last_name é obrigatório");
    }
    if (user.getEmail() == null || user.getEmail().isBlank()) {
      throw new IllegalArgumentException("email é obrigatório");
    }
  }
}
