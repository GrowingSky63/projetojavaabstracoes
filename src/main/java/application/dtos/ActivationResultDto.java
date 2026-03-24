package application.dtos;

public class ActivationResultDto {
  private String action;
  private Long userId;
  private Long subscriptionId;
  private String message;

  public ActivationResultDto(String action, Long userId, Long subscriptionId, String message) {
    this.action = action;
    this.userId = userId;
    this.subscriptionId = subscriptionId;
    this.message = message;
  }

  public String getAction() {
    return action;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getSubscriptionId() {
    return subscriptionId;
  }

  public String getMessage() {
    return message;
  }
}
