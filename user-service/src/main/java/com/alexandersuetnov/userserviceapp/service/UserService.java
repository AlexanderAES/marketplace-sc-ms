package com.alexandersuetnov.userserviceapp.service;

import com.alexandersuetnov.userserviceapp.dto.UserDTO;
import com.alexandersuetnov.userserviceapp.exception.UserExistException;
import com.alexandersuetnov.userserviceapp.exception.UserNotFoundException;
import com.alexandersuetnov.userserviceapp.model.User;
import com.alexandersuetnov.userserviceapp.model.enums.Role;
import com.alexandersuetnov.userserviceapp.payload.request.SignupRequest;
import com.alexandersuetnov.userserviceapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {

    private final EmailService mailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User createUser(SignupRequest userIn) {
        User user = new User();
        user.setEmail(userIn.getEmail());
        user.setUsername(userIn.getUsername());
        user.setName(userIn.getName());
        user.setPhoneNumber(userIn.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        user.setActivationCode(UUID.randomUUID().toString());
        sendMessage(user);
        log.info("Send message to email {}", userIn.getEmail());

        try {
            log.info("Saving user {}", userIn.getEmail());

            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Error during registration. {}", e.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exist. Please check credentials");
        }
    }


    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Market place. Please, visit next link: http://localhost:8080/users/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    public boolean activateUser(String code) {
        User user = userRepository.findUserByActivationCode(code);
        if (user == null) {
            return false;
        }
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setName(userDTO.getName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        log.info("Update user with email {}", user.getEmail());
        return userRepository.save(user);

    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));

    }

    public User getUserById(Long id) {
        return userRepository.findUserById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public void banUserById(String userId) {
        User user = userRepository.findUserById(Long.parseLong(userId)).orElseThrow(() -> new UserNotFoundException(Long.parseLong(userId)));
        if (user != null) {
            if (user.isActive()) {
                user.setActive(false);
                log.info("Ban user with id = {};", user.getId());
            } else {
                user.setActive(true);
                log.info("Unban user with id = {};", user.getId());
            }
        }
        userRepository.save(user);
    }

    public boolean сheckUserEnableDisable(String email) {
        User user = userRepository.findUserIdByEmail(email);
        log.info("Сhecking the user's ban with id = {};", user.getId());
        return user.isActive();
    }


    public List<User> getListUsers() {
        return userRepository.findAll();
    }
}
