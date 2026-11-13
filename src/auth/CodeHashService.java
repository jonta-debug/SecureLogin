package auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class CodeHashService {

    private final SecretKeySpec key;

    public CodeHashService() {
        String secret = System.getenv("TWO_FACTOR_SECRET");
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "TWO_FACTOR_SECRET is missing"
            );
        }

        byte[] keyBytes;
        try{
            keyBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e){
            throw new IllegalStateException(
                    "TWO_FACTOR_SECRET is not valid Base64"
            );
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "WO_FACTOR_SECRET must contain at least 32 bytes"
            );
        }
        this.key = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );
    }

    public String createCodeHash(long userId, String code) {
        if (code == null || !code.matches("[0-9]{6}")) {
            throw new IllegalArgumentException(
                    "Code must contain exactly six digits"
            );
        }

        try {

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);

            String input = userId + ":" + code;
            byte[] result = mac.doFinal(
                    input.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder().encodeToString(result);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Could not calculate HMAC", e
            );
        }
    }
}
