package com.alexandersuetnov.userserviceapp.controller;

import com.alexandersuetnov.userserviceapp.dto.UserDTO;
import com.alexandersuetnov.userserviceapp.mappers.UserMapper;
import com.alexandersuetnov.userserviceapp.model.User;
import com.alexandersuetnov.userserviceapp.payload.request.LoginRequest;
import com.alexandersuetnov.userserviceapp.payload.request.SignupRequest;
import com.alexandersuetnov.userserviceapp.payload.response.JWTTokenSuccessResponse;
import com.alexandersuetnov.userserviceapp.payload.response.MessageResponse;
import com.alexandersuetnov.userserviceapp.security.JWTTokenProvider;
import com.alexandersuetnov.userserviceapp.security.SecurityConstants;
import com.alexandersuetnov.userserviceapp.service.UserService;
import com.alexandersuetnov.userserviceapp.validations.ResponseErrorValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ResponseErrorValidation responseErrorValidation;
    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        if (userService.сheckUserEnableDisable(loginRequest.getEmail())) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generatedToken(authentication);

            return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User baned, or did not confirm the registration");
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        userService.createUser(signupRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<MessageResponse> confirm(@PathVariable("token") String token) {
        userService.activateUser(token);
        return new ResponseEntity<>(new MessageResponse("User was activate"), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        UserDTO userDTO = UserMapper.INSTANCE.UserToUserDTO(user);
        log.info("Getting the current authorized user with id {} ", user.getId());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId) {
        User user = userService.getUserById(Long.parseLong(userId));
        UserDTO userDTO = UserMapper.INSTANCE.UserToUserDTO(user);
        log.info("Get profile user with id {} ", user.getId());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        User user = userService.updateUser(userDTO, principal);

        UserDTO userUpdated = UserMapper.INSTANCE.UserToUserDTO(user);
        log.info("Update profile user with id {} ", user.getId());
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    @PutMapping("/ban/{userId}")
    public ResponseEntity<Object> userBan(@PathVariable("userId") String userId) {
        userService.banUserById(userId);
        log.info("User activation changed with id {} ", userId);
        return ResponseEntity.status(HttpStatus.OK).body("User activation changed");
    }

    @GetMapping("/all/users")
    public ResponseEntity<List<UserDTO>> getListUsers() {
        List<UserDTO> userDTOList = userService.getListUsers()
                .stream()
                .map(UserMapper.INSTANCE::UserToUserDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

}
