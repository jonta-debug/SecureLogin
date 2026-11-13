package model;

import java.time.Instant;
import java.security.SecureRandom;
import java.util.UUID;

public class VerificationCode {
    private long id;
    private long userId;
    private String codeHash;
    private Instant expiresAt;
    private int attempts;
    private boolean used;
    private final UUID challengeId;



    //CREATE
    public VerificationCode(long id, long userId, String codeHash, boolean used) {
        this.id = id;
        this.userId = userId;
        this.codeHash = codeHash;
        this.expiresAt = Instant.now().plusSeconds(300);
        this.attempts = 0;
        this.used = used;
        this.challengeId = UUID.randomUUID();
    }


    //RETREIVE
    public VerificationCode(
            long id, long userId,
            String codeHash, Instant expiresAt,
            int attempts, boolean used, UUID challengeId
    ) {
        this.id = id;
        this.userId = userId;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.attempts = attempts;
        this.used = used;
        this.challengeId = challengeId;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public boolean isUsed() {
        return used;
    }

    public UUID getChallengeId() {
        return challengeId;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
