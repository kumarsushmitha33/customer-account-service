package account.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import account.client.CustomerClient;
import account.entity.Account;
import account.exception.CustomerServiceUnavailableException;
import account.exception.ResourceNotFoundException;
import account.kafka.AccountEventProducer;
import account.repository.AccountRepository;
import common.events.AccountCreatedEvent;

@Service
public class AccountService {

	@Autowired
	private AccountRepository repo;
	
	@Autowired
    private CustomerClient customerClient;  // ✅ Injected Spring bean
	
	@Autowired
	private AccountEventProducer producer;
	
//	public List<Account> getAccountByCustomer(Long CustomerId) {
//		return repo.findByCustomerId(CustomerId);
//	}
	
//	public Account createAccount(Account account) {
//		return repo.save(account);	
//	}
//    public Account createAccount(Account acc, String token) {
//        boolean valid = customerClient.validateCustomer(acc.getCustomerId(), token); // ✅ instance call
//        if (!valid) {
//            throw new RuntimeException("Invalid Customer ID: " + acc.getCustomerId());
//        }
//        return repo.save(acc);
//    }
//	
//	public Account updateBalance(Long id, double newBalance) {
//		Account acc = repo.findById(id)
//				.orElseThrow (() -> new RuntimeException("Account not found"));
//		acc.setBalance(newBalance);
//		return repo.save(acc);
//	}
//	
//	public void deleteAccount(Long id) {
//		repo.deleteById(id);
//	}
	
	public List<Account> getAllAccounts() {
	    return repo.findAll();
	}
	
//	public Account createAccount(Account acc, String token) {
//        boolean valid = customerClient.validateCustomer(acc.getCustomerId(), token);
//        if (!valid) {
//            throw new IllegalArgumentException("Invalid Customer ID: " + acc.getCustomerId());
//        }
//        return repo.save(acc);
//    }
	 // ✅ Create account — includes validation logic
    public Account createAccount(Account acc, String token) {
        boolean valid;

        try {
            valid = customerClient.validateCustomer(acc.getCustomerId(), token);
        } catch (Exception e) {
            // If circuit breaker triggered fallback or service down
            throw new CustomerServiceUnavailableException(
                "Customer Service is temporarily unavailable. Please try again later."
            );
        }

        if (!valid) {
            throw new ResourceNotFoundException("Customer not found with ID: " + acc.getCustomerId());
        }

        Account saved = repo.save(acc); 
     // Create event object
        AccountCreatedEvent event = new AccountCreatedEvent(
            "ACCOUNT_CREATED",
            acc.getCustomerId(),
            acc.getAccountNumber(),
            LocalDateTime.now()
        );

        // Send event
        producer.sendAccountCreatedEvent(event);

        return saved;
//     // ✅ Send event to Kafka
//        String eventMessage = "Account created for Customer ID: " + acc.getCustomerId() +
//                              ", Account Number: " + acc.getAccountNumber();
//        //producer.sendAccountCreatedEvent(eventMessage);
//        producer.sendAccountCreatedEvent(acc.getCustomerId(), acc.getAccountNumber());
//
//        return saved;
    }


    public Account updateBalance(Long id, double newBalance) {
        Account acc = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for ID: " + id));
        acc.setBalance(newBalance);
        return repo.save(acc);
    }

    public void deleteAccount(Long id) {
        Account acc = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for ID: " + id));
        repo.delete(acc);
    }
    public Optional<Account> getAccountById(Long id) {
        return repo.findById(id);
    }
    public List<Account> getAccountByCustomer(Long customerId) {
        List<Account> accounts = repo.findByCustomerId(customerId);

        // ✅ Explicitly handle case when no accounts exist for given customer
        if (accounts == null || accounts.isEmpty()) {
            throw new ResourceNotFoundException("No accounts found for customer ID: " + customerId);
        }

        return accounts;
    }
}
