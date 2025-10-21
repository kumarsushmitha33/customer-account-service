package customer.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	List<Customer> findByUserId(Long userId);
}
