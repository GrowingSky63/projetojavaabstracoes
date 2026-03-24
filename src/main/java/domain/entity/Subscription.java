package domain.entity;

import java.time.LocalDateTime;

public class Subscription {
  private Long id;
  private String subscriptionId;
  private Long userId;
  private Long planId;
  private String paymentMethod;
  private LocalDateTime paymentApproveDate;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String status;

  public Subscription() {
  }

  public Subscription(
      Long id,
      String subscriptionId,
      Long userId,
      Long planId,
      String paymentMethod,
      LocalDateTime paymentApproveDate,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String status) {
    this.id = id;
    this.subscriptionId = subscriptionId;
    this.userId = userId;
    this.planId = planId;
    this.paymentMethod = paymentMethod;
    this.paymentApproveDate = paymentApproveDate;
    this.startDate = startDate;
    this.endDate = endDate;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }

  public void setSubscriptionId(String subscriptionId) {
    this.subscriptionId = subscriptionId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getPlanId() {
    return planId;
  }

  public void setPlanId(Long planId) {
    this.planId = planId;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public LocalDateTime getPaymentApproveDate() {
    return paymentApproveDate;
  }

  public void setPaymentApproveDate(LocalDateTime paymentApproveDate) {
    this.paymentApproveDate = paymentApproveDate;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
