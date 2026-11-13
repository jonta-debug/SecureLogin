package model;

import java.util.Date;

public class User {
    private long id;
    private String email;
    private String passwordHash;
    private boolean verified;
    private Date timeStamp;

    public User(long id, String email, String passwordHash, boolean verified) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.verified = verified;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isVerified() {
        return verified;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
}
