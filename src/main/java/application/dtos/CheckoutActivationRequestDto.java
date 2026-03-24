package application.dtos;

public class CheckoutActivationRequestDto {
  private String firstName;
  private String lastName;
  private String email;
  private String plan;
  private String subscriptionId;
  private String paymentMethod;
  private String paymentAproveDate;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPlan() {
    return plan;
  }

  public void setPlan(String plan) {
    this.plan = plan;
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }

  public void setSubscriptionId(String subscriptionId) {
    this.subscriptionId = subscriptionId;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getPaymentAproveDate() {
    return paymentAproveDate;
  }

  public void setPaymentAproveDate(String paymentAproveDate) {
    this.paymentAproveDate = paymentAproveDate;
  }
}
