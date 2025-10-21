package customer.service;

import customer.entity.Customer;
import customer.exception.ResourceNotFoundException;
import customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    // ✅ Get all customers
    public List<Customer> getAll() {
        List<Customer> customers = repository.findAll();
        if (customers.isEmpty()) {
            throw new ResourceNotFoundException("No customers found in the database");
        }
        return customers;
    }

    // ✅ Create or update customer
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    // ✅ Get customer by ID
    public Customer getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
    }

 /// ✅ Delete customer safely
    public void delete(Long id) {
        Customer existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cannot delete. Customer not found with ID: " + id));

        repository.delete(existing);
    }
}