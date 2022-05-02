package nz.ac.canterbury.seng302.identityprovider.service;

import org.mindrot.jbcrypt.BCrypt;

/**
 * The service class to use the BCrypt library to salt and hash passwords
 */
public class Hasher {

    /**
     * Checks a plaintext password with a hashed password
     * @param password plaintext password
     * @param hashedPassword hashed password
     * @return boolean of if they match
     */
    public static boolean verify(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * hashes the password with a random salt
     * @param password the plain password to hash
     * @return the hashed password
     */
    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        String hashed = BCrypt.hashpw(password, salt);

        return hashed;
    }
}
