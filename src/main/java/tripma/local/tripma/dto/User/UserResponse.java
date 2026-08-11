package tripma.local.tripma.dto.User;

import tripma.local.tripma.entity.User;

public record UserResponse(
    Integer userId,
    String fullName,
    String email,
    String phone,
    String authProvider
) {
    public static UserResponse fromEntity(User user){
        return new UserResponse(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.getAuthProvider()
        );
    }  
}