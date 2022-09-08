package account.dto;

import account.entities.Role;

import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class UserDTO {

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

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    public UserDTO() {
    }

    public UserDTO(String name, String lastname, String email, String password, List<Role> roles) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roles = roles;
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
        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
