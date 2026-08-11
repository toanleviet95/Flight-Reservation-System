package tripma.local.tripma.service;

import org.springframework.stereotype.Service;
import tripma.local.tripma.dto.User.UserRequest;
import tripma.local.tripma.dto.User.UserResponse;
import tripma.local.tripma.entity.User;
import tripma.local.tripma.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
