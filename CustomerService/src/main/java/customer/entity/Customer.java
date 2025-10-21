package customer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String city;
    
 // Link customer to user who created it -- JWT
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

// Lombok annotations (@Data, @NoArgsConstructor, @AllArgsConstructor) remove the need 
// for getters, setters, constructors, etc.