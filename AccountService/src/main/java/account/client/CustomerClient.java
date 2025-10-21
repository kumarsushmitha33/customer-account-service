package account.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerClient {

    private final RestTemplate restTemplate;
    private static final String CUSTOMER_SERVICE_CB = "customerServiceCB"; // circuit breaker name

    @Autowired
    public CustomerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = CUSTOMER_SERVICE_CB, fallbackMethod = "fallbackCustomerValidation")
    @Retry(name = CUSTOMER_SERVICE_CB)
    @RateLimiter(name = CUSTOMER_SERVICE_CB)
    public boolean validateCustomer(Long id, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();

            if (token != null && !token.startsWith("Bearer ")) {
                token = "Bearer " + token;
            }
            headers.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(null, headers);

            // ✅ Call CustomerService via Eureka
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://CustomerService/api/customers/" + id,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();

        } catch (HttpClientErrorException.NotFound e) {
            // ✅ CustomerService is up, but customer doesn’t exist
            System.out.println("❌ Customer not found with ID: " + id);
            return false;

        } catch (Exception e) {
            // ✅ Network failure, timeout, or connection refused → triggers fallback
            System.out.println("⚠️ CustomerService unavailable: " + e.getMessage());
            throw e;
        }
    }

    // ✅ Fallback executed when circuit opens or retries fail
    public boolean fallbackCustomerValidation(Long id, String token, Throwable throwable) {
        System.out.println("🚨 Fallback triggered for CustomerService! Reason: " + throwable.getMessage());
        return false; // fallback response (safe failure)
    }
}
    // ✅ Fallback method (executed when CustomerService is
// *********************************************************************************
//package account.client;
//
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class CustomerClient {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public boolean validateCustomer(Long id, String token) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//
//            // ✅ Always ensure Bearer prefix
//            if (token != null && !token.startsWith("Bearer ")) {
//                token = "Bearer " + token;
//            }
//            headers.set("Authorization", token);
//
//            // ✅ Explicitly say: no body, but with headers
//            HttpEntity<String> entity = new HttpEntity<>(null, headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    "http://localhost:8080/api/customers/" + id,
//                    HttpMethod.GET,
//                    entity,
//                    String.class
//            );
//
//            System.out.println("✅ CustomerService responded with: " + response.getStatusCode());
//            return response.getStatusCode().is2xxSuccessful();
//
//        } catch (Exception e) {
//            System.out.println("❌ Customer validation failed: " + e.getMessage());
//            return false;
//        }
//    }
//}