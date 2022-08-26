package account.controllers;

import account.entities.Role;
import account.entities.User;
import account.exceptions.AccountServiceException;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @GetMapping("/api/empl/payment")
//    public Map<String, Object> testAuth(@AuthenticationPrincipal User user) {
//        return createResponse(user);
//    }

    @PostMapping("/api/auth/signup")
    public Map<String, Object> createUser(@Valid @RequestBody UserRegistration userRegistration) {

        String password = userRegistration.getPassword();
        if (isPasswordInBreachedList(password)) {
            throw new AccountServiceException("The password is in the hacker's database!");
        }

        String username = userRegistration.getEmail().toLowerCase();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new AccountServiceException("User exist!");
        }

        User user = new User();
        user.setUsername(username);
        user.setName(userRegistration.getName());
        user.setLastname(userRegistration.getLastname());
        user.setEmail(userRegistration.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.grantAuthority(Role.ROLE_USER);
        userRepository.save(user);

        return createResponse(user);
    }

    @PostMapping("/api/auth/changepass")
    public Map<String, Object> changeUserPassword(@Valid @RequestBody NewUserPassword newPassword) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        String password = newPassword.getNew_password();
        if (isPasswordInBreachedList(password)) {
            throw new AccountServiceException("The password is in the hacker's database!");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new AccountServiceException("The passwords must be different!");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("email", user.getUsername());
//        response.put("email", user.getEmail());
        response.put("status", "The password has been updated successfully");

        return response;
    }

    public Map<String, Object> createResponse(User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("lastname", user.getLastname());
        response.put("email", user.getEmail());

        return response;
    }

    public boolean isPasswordInBreachedList(String password) {

        String[] breachedList = new String[] {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember",
                "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

        for (var breachedPassword : breachedList) {
            if (breachedPassword.equals(password)) {
                return true;
            }
        }

        return false;
    }
}


class NewUserPassword {
    @NotBlank(message = "Empty password field!")
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String new_password;

    public NewUserPassword() {
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}

class UserRegistration {

    @NotBlank(message = "Empty name field!")
    private String name;
    @NotBlank(message = "Empty lastname field!")
    private String lastname;
    @NotBlank(message = "Empty email field!")
    @Email
    @Pattern(regexp = ".+@acme.com", message = "Wrong email!")
    private String email;
    @NotBlank(message = "Empty password field!")
    @Size(min = 12, message = "The password length must be at least 12 chars!")
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
