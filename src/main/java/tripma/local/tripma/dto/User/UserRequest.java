package tripma.local.tripma.dto.User;

public record UserRequest(
    String fullName,
    String email,
    String passwordHash,
    String phone,
    String authProvider
) {
    
}