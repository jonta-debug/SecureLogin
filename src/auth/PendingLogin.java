package auth;

import java.util.UUID;

public record PendingLogin(
        long userId,
        UUID challengeId
) {
}
