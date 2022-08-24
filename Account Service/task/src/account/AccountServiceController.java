package account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AccountServiceController {

    @GetMapping("/api/empl/payment/")
    public String testAuth() {
        return "authenticated is accessed";
    }

    @PostMapping("/api/auth/signup")
    public BaseUser postUser(@RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getLastname() == null || user.getLastname().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty() ||
                !user.getEmail().endsWith("@acme.com")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return new BaseUser(user);
    }
}


class User extends BaseUser {
    private String password;

    public User() {
        super();
    }

    public User(String name, String lastname, String email, String password) {
        super(name, lastname, email);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class BaseUser {
    private String name;
    private String lastname;
    private String email;

    public BaseUser() {
    }

    public BaseUser(String name, String lastname, String email) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }

    public BaseUser(User user) {
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
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