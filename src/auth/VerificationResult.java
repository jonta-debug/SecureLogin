package auth;

import java.util.UUID;

public record VerificationResult(
        UUID challengeId,
        String code
) {

}
