package account.dto;

import account.entities.Role;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class UserDTO2 {

    private int id;
    private String name;
    private String lastname;
    private String email;
    private LinkedList<Role> roles;
//    private List<Role> roles;

    public UserDTO2() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        this.email = email.toLowerCase();
    }

    public List<Role> getRoles() {
        return roles;
    }

//    public void setRoles(List<Role> roles) {
    public void setRoles(LinkedList<Role> roles) {
        this.roles = roles;
        this.roles.sort(Comparator.naturalOrder());
    }
}
