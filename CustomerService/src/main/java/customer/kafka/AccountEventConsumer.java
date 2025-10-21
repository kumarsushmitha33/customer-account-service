package customer.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import common.events.AccountCreatedEvent;

@Service
public class AccountEventConsumer {

    @KafkaListener(topics = "account-created-topic", groupId = "customer-service-group")
    public void consume(AccountCreatedEvent event) {
        System.out.println("📩 Received Account Event: " + event);
    }
}

// *************************************
//package customer.kafka;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AccountEventConsumer {
//
//    @KafkaListener(topics = "account-created-topic", groupId = "customer-service-group")
//    public void consumeAccountCreatedEvent(String message) {
//        System.out.println("📩 Received Account Event: " + message);
//
//        // Optionally: update customer profile, log event, trigger notifications, etc.
//    }
//}