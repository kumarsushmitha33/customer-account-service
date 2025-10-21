package common.events;

import java.time.LocalDateTime;

public class AccountCreatedEvent {
    private String eventType;
    private Long customerId;
    private String accountNumber;
    private LocalDateTime timestamp;

    public AccountCreatedEvent() {}

    public AccountCreatedEvent(String eventType, Long customerId, String accountNumber, LocalDateTime timestamp) {
        this.eventType = eventType;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.timestamp = timestamp;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "AccountCreatedEvent{" +
                "eventType='" + eventType + '\'' +
                ", customerId=" + customerId +
                ", accountNumber='" + accountNumber + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}