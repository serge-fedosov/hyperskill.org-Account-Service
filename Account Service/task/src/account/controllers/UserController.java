package account.controllers;

import account.dto.UserDTO;
import account.dto.UserDTO2;
import account.entities.User;
import account.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/api/auth/signup")
    public UserDTO2 createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.create(userDTO);
    }

    @PostMapping("/api/auth/changepass")
    public Map<String, String> changePassword(@Valid @RequestBody NewUserPassword newPassword) {
        // TODO FIX IT! передавать пароль в строке, убрать NewUserPassword
        String password = newPassword.getNew_password();
        return userService.changePassword(password);
    }

    public Map<String, Object> createResponse(User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("lastname", user.getLastname());
        response.put("email", user.getEmail());
        response.put("roles", user.getRoles());

        return response;
    }

    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private UserDTO2 convertToUserDTO2(User user) {
        return modelMapper.map(user, UserDTO2.class);
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
