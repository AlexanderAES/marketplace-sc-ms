package com.alexandersuetnov.userserviceapp.controller;

import com.alexandersuetnov.userserviceapp.payload.request.LoginRequest;
import com.alexandersuetnov.userserviceapp.payload.request.SignupRequest;
import com.alexandersuetnov.userserviceapp.payload.response.JWTTokenSuccessResponse;
import com.alexandersuetnov.userserviceapp.payload.response.MessageResponse;
import com.alexandersuetnov.userserviceapp.security.JWTTokenProvider;
import com.alexandersuetnov.userserviceapp.security.SecurityConstants;
import com.alexandersuetnov.userserviceapp.service.UserService;
import com.alexandersuetnov.userserviceapp.validations.ResponseErrorValidation;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
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
}
