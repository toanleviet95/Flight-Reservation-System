package tripma.local.tripma.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tripma.local.tripma.dto.User.UserRequest;
import tripma.local.tripma.dto.User.UserResponse;
import tripma.local.tripma.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

}