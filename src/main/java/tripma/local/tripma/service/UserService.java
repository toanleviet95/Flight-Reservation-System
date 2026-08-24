package tripma.local.tripma.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tripma.local.tripma.dto.User.UserRequest;
import tripma.local.tripma.dto.User.UserResponse;
import tripma.local.tripma.entity.User;
import tripma.local.tripma.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + userRequest.email());
        }

        User user = new User();
        user.setFullName(userRequest.fullName());
        user.setEmail(userRequest.email());
        user.setPasswordHash(passwordEncoder.encode(userRequest.passwordHash()));
        user.setPhone(userRequest.phone());
        user.setAuthProvider(userRequest.authProvider());

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public UserResponse login(UserRequest userRequest) {
        User user = userRepository.findByEmail(userRequest.email())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "User not found with email: " + userRequest.email()));

        if (!passwordEncoder.matches(userRequest.passwordHash(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }
        return UserResponse.fromEntity(user);
    }
}
