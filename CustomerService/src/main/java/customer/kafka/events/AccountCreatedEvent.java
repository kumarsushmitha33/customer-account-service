//package customer.kafka.events;
//
//import java.time.LocalDateTime;
//
//public class AccountCreatedEvent {
//    private String eventType;
//    private Long customerId;
//    private String accountNumber;
//    private LocalDateTime timestamp;
//
//    // ✅ Required default constructor
//    public AccountCreatedEvent() {}
//
//    // ✅ Getters/Setters
//    public String getEventType() { return eventType; }
//    public void setEventType(String eventType) { this.eventType = eventType; }
//
//    public Long getCustomerId() { return customerId; }
//    public void setCustomerId(Long customerId) { this.customerId = customerId; }
//
//    public String getAccountNumber() { return accountNumber; }
//    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
//
//    public LocalDateTime getTimestamp() { return timestamp; }
//    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
//
//    @Override
//    public String toString() {
//        return "AccountCreatedEvent{" +
//                "eventType='" + eventType + '\'' +
//                ", customerId=" + customerId +
//                ", accountNumber='" + accountNumber + '\'' +
//                ", timestamp=" + timestamp +
//                '}';
//    }
//}