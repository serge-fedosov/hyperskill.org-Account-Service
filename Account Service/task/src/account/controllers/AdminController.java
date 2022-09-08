package account.controllers;

import account.dto.UserAccessDTO;
import account.dto.UserDTO2;
import account.dto.UserRoleDTO;
import account.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/admin/user")
    public Object getUsers() {
        return userService.findAllUsers();
    }

    //    public Map<String, String> delete(@RequestParam(required = true) String userEmail) {
    @DeleteMapping("/api/admin/user/{userEmail}")
    public Map<String, String> delete(@PathVariable String userEmail) {
        userService.delete(userEmail);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("user", userEmail);
        response.put("status", "Deleted successfully!");

        return response;
    }

    @PutMapping("/api/admin/user/role")
    public UserDTO2 setRole(@RequestBody(required = true) UserRoleDTO userRoleDTO) {
        return userService.setRole(userRoleDTO);
    }

    @PutMapping("/api/admin/user/access")
    public Map<String, String> setAccess(@RequestBody(required = true) UserAccessDTO userAccessDTO) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", userService.setAccess(userAccessDTO));

        return response;
    }
}
