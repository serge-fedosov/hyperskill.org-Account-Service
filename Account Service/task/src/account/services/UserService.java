package account.services;

import account.dto.UserAccessDTO;
import account.dto.UserDTO;
import account.dto.UserDTO2;
import account.dto.UserRoleDTO;
import account.entities.Event;
import account.entities.Events;
import account.entities.Role;
import account.entities.User;
import account.exceptions.AccountServiceException;
import account.exceptions.NotFoundException;
import account.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EventService eventService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, EventService eventService, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        Optional<User> user = userRepository.findByUsername(s);
        Optional<User> user = userRepository.findByUsername(s.toLowerCase());

        if (user.isPresent()){
            return user.get();
        } else {
            throw new UsernameNotFoundException(String.format("Username[%s] not found"));
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase());
    }

    public List<UserDTO2> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO2> usersDTO2List = new ArrayList<>();

        for (User user : users) {
            usersDTO2List.add(modelMapper.map(user, UserDTO2.class));
        }

        return usersDTO2List;
    }



//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<Person> person = peopleRepository.findByUsername(username);
//
//        if (person.isEmpty()) {
//            throw new UsernameNotFoundException("User not found");
//        }
//
//        return new PersonDetails(person.get());
//    }

    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public UserDTO2 create(UserDTO userDTO) {

        String password = userDTO.getPassword();
        if (isPasswordInBreachedList(password)) {
            throw new AccountServiceException("The password is in the hacker's database!");
        }

        String username = userDTO.getEmail().toLowerCase();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            throw new AccountServiceException("User exist!");
        }

        User user = modelMapper.map(userDTO, User.class);
        enrichUser(user);
        userRepository.save(user);

        Event event = new Event(LocalDateTime.now(), Events.CREATE_USER.toString(), "Anonymous", username, "/api/auth/signup");
        eventService.save(event);

        return modelMapper.map(user, UserDTO2.class);
    }

    @Transactional
    public void delete(String userEmail) {

        Optional<User> optionalUser = userRepository.findByUsername(userEmail.toLowerCase());

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }

        User user = optionalUser.get();

        if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            throw new AccountServiceException("Can't remove ADMINISTRATOR role!");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAction = (User) auth.getPrincipal();

        Event event = new Event(LocalDateTime.now(), Events.DELETE_USER.toString(), userAction.getUsername(), user.getUsername(), "/api/admin/user");
        eventService.save(event);

        // TODO FIX IT!
        userRepository.delete(user);
    }

    @Transactional
    public UserDTO2 setRole(UserRoleDTO userRoleDTO) {

        String username = userRoleDTO.getUser().toLowerCase();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }

        User user = optionalUser.get();
        Role role = null;

        try {
            role = Role.valueOf("ROLE_" + userRoleDTO.getRole());
        } catch (Exception e) {
            throw new NotFoundException("Role not found!");
        }


        String objectStr = null;
        Events eventRole = null;
        if ("REMOVE".equals(userRoleDTO.getOperation())) {

            if (Role.ROLE_ADMINISTRATOR.equals(role)) {
                throw new AccountServiceException("Can't remove ADMINISTRATOR role!");
            }

            if (!user.getRoles().contains(role)) {
                throw new AccountServiceException("The user does not have a role!");
            }

            if (user.getRoles().size() == 1) {
                throw new AccountServiceException("The user must have at least one role!");
            }

            user.getRoles().remove(role);

            objectStr = "Remove role " + role.toString().substring(5) + " from " + username;
            eventRole = Events.REMOVE_ROLE;
        } else {

            if ((Role.ROLE_ADMINISTRATOR.equals(role) && (user.getRoles().contains(Role.ROLE_USER) || user.getRoles().contains(Role.ROLE_ACCOUNTANT)))
                    || (Role.ROLE_ACCOUNTANT.equals(role) && user.getRoles().contains(Role.ROLE_ADMINISTRATOR))
                    || (Role.ROLE_AUDITOR.equals(role) && user.getRoles().contains(Role.ROLE_ADMINISTRATOR))
                    || (Role.ROLE_USER.equals(role) && user.getRoles().contains(Role.ROLE_ADMINISTRATOR))) {
                throw new AccountServiceException("The user cannot combine administrative and business roles!");
            }

            user.getRoles().add(role);
            objectStr = "Grant role " + role.toString().substring(5) + " to " + username;
            eventRole = Events.GRANT_ROLE;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAction = (User) auth.getPrincipal();

        Event event = new Event(LocalDateTime.now(), eventRole.toString(), userAction.getUsername(), objectStr, "/api/admin/user/role");
        eventService.save(event);

        return modelMapper.map(user, UserDTO2.class);
    }

    @Transactional
    public String setAccess(UserAccessDTO userAccessDTO) {

        String username = userAccessDTO.getUser().toLowerCase();
        String status = "User " + username;
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }

        User user = optionalUser.get();

//        Role role = null;
//
//        try {
//            role = Role.valueOf("ROLE_" + userRoleDTO.getRole());
//        } catch (Exception e) {
//            throw new NotFoundException("Role not found!");
//        }

        if ("LOCK".equals(userAccessDTO.getOperation())) {
            status += " locked!";

            if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                throw new AccountServiceException("Can't lock the ADMINISTRATOR!");
            }

//            if (!user.getRoles().contains(role)) {
//                throw new AccountServiceException("The user does not have a role!");
//            }
//
//            if (user.getRoles().size() == 1) {
//                throw new AccountServiceException("The user must have at least one role!");
//            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User userAction = (User) auth.getPrincipal();

            Event event = new Event(LocalDateTime.now(), Events.UNLOCK_USER.toString(), userAction.getUsername(), "Lock user " + user.getUsername(), "/api/admin/user/access");
            eventService.save(event);

            user.setAccountNonLocked(false);
        } else {
            status += " unlocked!";

//            if ((Role.ROLE_ADMINISTRATOR.equals(role) && (user.getRoles().contains(Role.ROLE_USER) || user.getRoles().contains(Role.ROLE_ACCOUNTANT)))
//                    || (Role.ROLE_ACCOUNTANT.equals(role) && user.getRoles().contains(Role.ROLE_ADMINISTRATOR))
//                    || (Role.ROLE_USER.equals(role) && user.getRoles().contains(Role.ROLE_ADMINISTRATOR))) {
//                throw new AccountServiceException("The user cannot combine administrative and business roles!");
//            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User userAction = (User) auth.getPrincipal();

            Event event = new Event(LocalDateTime.now(), Events.UNLOCK_USER.toString(), userAction.getUsername(), "Unlock user " + user.getUsername(), "/api/admin/user/access");
            eventService.save(event);

            user.setAccountNonLocked(true);
            user.setFailedLogin(0);
        }

        return status;
    }


    @Transactional
    public void savePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public void save(User user) {
        enrichUser(user);

        userRepository.save(user);
    }

    private void enrichUser(User user) {
        user.setUsername(user.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userRepository.count() == 0) {
            user.setRoles(Collections.singletonList(Role.ROLE_ADMINISTRATOR));
        } else {
            user.setRoles(Collections.singletonList(Role.ROLE_USER));
        }
    }

    public static boolean isPasswordInBreachedList(String password) {

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

    @Transactional
    public Map<String, String> changePassword(String password) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        if (isPasswordInBreachedList(password)) {
            throw new AccountServiceException("The password is in the hacker's database!");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new AccountServiceException("The passwords must be different!");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        Event event = new Event(LocalDateTime.now(), Events.CHANGE_PASSWORD.toString(), user.getUsername(), user.getUsername(), "/api/auth/changepass");
        eventService.save(event);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("email", user.getUsername());
        response.put("status", "The password has been updated successfully");

        return response;
    }

    @Transactional
    public boolean loginFailed(String username) {

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            int failedLogin = user.getFailedLogin() + 1;
            user.setFailedLogin(failedLogin);
            if (failedLogin > 5 && !user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {

                Event event = new Event(LocalDateTime.now(), Events.BRUTE_FORCE.toString(), user.getUsername(), "/api/empl/payment", "/api/empl/payment");
                eventService.save(event);

                Event event2 = new Event(LocalDateTime.now(), Events.LOCK_USER.toString(), user.getUsername(), "Lock user " + user.getUsername(), "/api/admin/user/access");
                eventService.save(event2);

                user.setAccountNonLocked(false);
                return true;
            }

        }

        return false;
    }
}
