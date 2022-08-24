package account.controllers;

import account.entities.Role;
import account.entities.User;
import account.exceptions.UserExistException;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class AccountServiceController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/api/empl/payment")
    public UserResponse testAuth(@AuthenticationPrincipal User user) {
        return new UserResponse(user);
    }

    @PostMapping("/api/auth/signup")
    public UserResponse createUser(@RequestBody UserRegistration userRegistration) {
        if (userRegistration.getName() == null || userRegistration.getName().isEmpty() || userRegistration.getLastname() == null || userRegistration.getLastname().isEmpty() ||
                userRegistration.getEmail() == null || userRegistration.getEmail().isEmpty() || userRegistration.getPassword() == null || userRegistration.getPassword().isEmpty() ||
                !userRegistration.getEmail().endsWith("@acme.com")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String username = userRegistration.getEmail().toLowerCase();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserExistException("User exist!");
        }

        User user = new User();
        user.setUsername(username);
        user.setName(userRegistration.getName());
        user.setLastname(userRegistration.getLastname());
        user.setEmail(userRegistration.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistration.getPassword()));
        user.grantAuthority(Role.ROLE_USER);
        userRepository.save(user);

        return new UserResponse(user);
    }
}

class UserResponse {
    private long id;
    private String name;
    private String lastname;
    private String email;

    public UserResponse() {
    }

    public UserResponse(long id, String name, String lastname, String email) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class UserRegistration {

    private String name;
    private String lastname;
    private String email;
    private String password;

    public UserRegistration() {
    }

    public UserRegistration(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
