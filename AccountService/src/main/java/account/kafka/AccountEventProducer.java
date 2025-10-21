package account.kafka;

import common.events.AccountCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountEventProducer {

    private static final String TOPIC = "account-created-topic";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendAccountCreatedEvent(AccountCreatedEvent event) {
        System.out.println("📤 Sending JSON to Kafka: " + event);
        kafkaTemplate.send(TOPIC, event);
    }
}
//package account.kafka;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AccountEventProducer {
//
//    private static final String TOPIC = "account-created-topic";
//
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    public void sendAccountCreatedEvent(String message) {
//        System.out.println("📤 Sending message to Kafka: " + message);
//        kafkaTemplate.send(TOPIC, message);
//    }
//}