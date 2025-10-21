package account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import account.entity.Account;

public interface AccountRepository extends JpaRepository<Account,Long>{
	List<Account> findByCustomerId(Long customerId);
}
