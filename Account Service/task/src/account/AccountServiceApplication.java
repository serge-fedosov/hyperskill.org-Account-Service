package account;

import account.entities.Role;
import account.entities.User;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}

@Component
class DemoCommandLineRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

//        User user = new User();
//        user.setUsername("user1");
//        user.setPassword(passwordEncoder.encode("password1"));
//        user.grantAuthority(Role.ROLE_ADMIN);
//
//        user.setName("User1");
//        user.setLastname("a");
//        user.setEmail("user@email.com");
//
//        userRepository.save(user);
    }
}
