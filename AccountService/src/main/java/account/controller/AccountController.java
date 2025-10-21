package account.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import account.entity.Account;
import account.exception.ResourceNotFoundException;
import account.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService service;
    
    @Autowired
    private Environment environment;

    // ✅ Fetch all accounts
    @GetMapping("/")
    public List<Account> getAllAccounts() {
        return service.getAllAccounts();
    }

    // ✅ Fetch by customer ID
    @GetMapping("/customer/{customerId}")
    public List<Account> getAccounts(@PathVariable Long customerId) {
        return service.getAccountByCustomer(customerId);
    }

    // ✅ Create new account
    @PostMapping
    public Account createAccount(@RequestBody Account acc,
                                 @RequestHeader("Authorization") String token) {
        return service.createAccount(acc, token);
    }
//    @PostMapping
//    public Account createAccount(@RequestBody Account acc) {
//        return service.createAccount(acc);
//    }

    // ✅ Update balance
    @PutMapping("/{id}/balance/{amount}")
    public Account updateBalance(@PathVariable Long id, @PathVariable double amount) {
        return service.updateBalance(id, amount);
    }
 // ✅ Get account by ID (Long)
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account = service.getAccountById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        return ResponseEntity.ok(account);
    }

    // ✅ Delete account
    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {
        service.deleteAccount(id);
        return "Account deleted successfully!";
    }
    
    @GetMapping("/test")
    public String test() {
        return "Hello from AccountService - port " + environment.getProperty("local.server.port");
    }
}