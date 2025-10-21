package customer.controller;

import customer.entity.Customer;
import customer.entity.User;
import customer.exception.ResourceNotFoundException;
import customer.repository.CustomerRepository;
import customer.repository.UserRepository;
import customer.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.access.prepost.PreAuthorize;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private CustomerService customerService; 
    
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/accounts") // Load balancing
    public String getAccounts() {
        // 👇 Use Eureka Service Name instead of host:port
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://AccountService/api/accounts/test",
                String.class);

        return "Response from AccountService: " + response.getBody();
    }
    
    // ✅ Get all customers belonging to the logged-in user
    @GetMapping
    public List<Customer> getAll(Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getName()));
        return customerRepo.findByUserId(user.getId());
    }

    // ✅ Create a customer linked to the logged-in user
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getName()));
        customer.setUser(user);
        return customerRepo.save(customer);
    }

    // ✅ Get ALL customers (for admin or internal service calls)
  
 // customer/controller/CustomerController.java
    // Only ADMIN can fetch all customers (admin view)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepo.findAll();
        if (customers.isEmpty()) throw new customer.exception.ResourceNotFoundException("No customers found");
        return customers;
    }
//    public List<Customer> getAllCustomers() {
//        List<Customer> customers = customerRepo.findAll();
//        if (customers.isEmpty()) {
//            throw new ResourceNotFoundException("No customers found in the database");
//        }
//        return customers;
//    }

    // ✅ Get a single customer by ID (used by AccountService validation)
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for ID: " + id));
    }
    
 // account controller example
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);  // ✅ Use the injected instance, not class name
        return ResponseEntity.ok("✅ Customer deleted successfully with ID: " + id);
    }
}
//package customer.controller;
//
//import customer.entity.Customer;
//import customer.entity.User;
//import customer.repository.CustomerRepository;
//import customer.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/customers")
//public class CustomerController {
//
//    @Autowired
//    private CustomerRepository customerRepo;
//
//    @Autowired
//    private UserRepository userRepo;
//
//    // ✅ Get all customers belonging to the logged-in user
//    @GetMapping
//    public List<Customer> getAll(Principal principal) {
//        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
//        return customerRepo.findByUserId(user.getId());
//    }
//
//    // ✅ Create a customer linked to the logged-in user
//    @PostMapping
//    public Customer createCustomer(@RequestBody Customer customer, Principal principal) {
//        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
//        customer.setUser(user);
//        return customerRepo.save(customer);
//    }
//
//    // ✅ Get ALL customers (for admin or internal service calls)
//    @GetMapping("/all")
//    public List<Customer> getAllCustomers() {
//        return customerRepo.findAll();
//    }
//
//    // ✅ Get a single customer by ID (used by AccountService validation)
//    @GetMapping("/{id}")
//    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
//        return customerRepo.findById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//}