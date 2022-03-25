package nz.ac.canterbury.seng302.identityprovider.service;

import org.mindrot.jbcrypt.BCrypt;


public class Hasher {
    /**
     * TODO:
     */


    public static boolean verify(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }


    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        String hashed = BCrypt.hashpw(password, salt);

        return hashed;
    }
}
