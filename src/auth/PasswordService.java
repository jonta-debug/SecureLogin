package auth;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordService {

    Argon2 argon2 = Argon2Factory.create(
            Argon2Factory.Argon2Types.ARGON2id
    );

    public String hashPassword(char[] password) {
        if(password == null) {
            return null;
        } else {
            try {
                return argon2.hash(3, 65536, 1, password);
            } finally {
                argon2.wipeArray(password);
            }
        }
    }


    public boolean verifyPassword(char[] password, String hash) {
        if(hash == null|| password == null){
            return false;
        }
        try{
            return argon2.verify(hash, password);
        }
        finally {
            argon2.wipeArray(password);
        }
    }
}
