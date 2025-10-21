package customer.controller;

import customer.entity.User;
import customer.exception.InvalidCredentialsException;
import customer.exception.UserAlreadyExistsException;
import customer.repository.UserRepository;
import customer.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;
    
 // customer/controller/AuthController.java (only changes shown)
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new customer.exception.UserAlreadyExistsException(
                "User already exists with username: " + user.getUsername());
        }
        if (user.getRole() == null) user.setRole(User.Role.USER); // default
        userRepo.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        User existing = userRepo.findByUsername(user.getUsername())
                .orElseThrow(() -> new customer.exception.InvalidCredentialsException("User not found"));

        if (!existing.getPassword().equals(user.getPassword())) {
            throw new customer.exception.InvalidCredentialsException("Invalid password");
        }

        String token = jwtUtil.generateToken(existing.getUsername(), existing.getRole().name()); // <-- include role
        return Map.of("token", token);
    }

//    @PostMapping("/register")
//    public String register(@RequestBody User user) {
//        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
//            throw new UserAlreadyExistsException("User already exists with username: " + user.getUsername());
//        }
//        userRepo.save(user);
//        return "User registered successfully!";
//    }
//
//    @PostMapping("/login")
//    public Map<String, String> login(@RequestBody User user) {
//        User existing = userRepo.findByUsername(user.getUsername())
//                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
//
//        if (!existing.getPassword().equals(user.getPassword())) {
//            throw new InvalidCredentialsException("Invalid password");
//        }
//
//        String token = jwtUtil.generateToken(existing.getUsername());
//        return Map.of("token", token);
//    }
}