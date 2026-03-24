package domain.repository;

import domain.entity.Subscription;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
  Subscription save(Subscription subscription);

  Optional<Subscription> findById(Long id);

  Optional<Subscription> findBySubscriptionId(String subscriptionId);

  List<Subscription> findByUserId(Long userId);

  List<Subscription> findAll();

  boolean deleteById(Long id);
}
