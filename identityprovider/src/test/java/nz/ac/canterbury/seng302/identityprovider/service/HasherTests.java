package nz.ac.canterbury.seng302.identityprovider.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdentityproviderApplicationTests { // TODO: ask if this should be called HasherTest class instead

    @Test
    void newSalt() {
        String password = "abc";
        String hashed1 = Hasher.hashPassword(password);
        String hashed2 = Hasher.hashPassword(password);
        assertFalse(hashed1.equals(hashed2));
    }

    @Test
    void correctDeHashing() {
        String password = "abc";
        String hashed1 = Hasher.hashPassword(password);
        String hashed2 = Hasher.hashPassword(password);
        assertTrue(Hasher.verify(password, hashed1));
        assertTrue(Hasher.verify(password, hashed2));
    }

    @ParameterizedTest
    @CsvSource({
        "PasswordA,$2a$12$nK.etMPLPHn95pnu7FnduOpfekLA1xx80s4xa13dCqX0S..nORb7K",
        "Password123,$2a$07$GCrfGOsmyad.IJ9aKtnc0e1VtmEVfv5fjXhtqmKNV15FbVcgadsqe",
        "Password123!@#$%^&*,$2a$07$3Wwf6ibjD48brF0LPVpSiuYkRhUna2g9B70JCKopz1vve2i/N/fT6",
        "Pass word A 1 !, $2a$07$It0ttKYuRDls0ambYFk.5e0c9qcNby1OURo5PjsshQ5YXjbJNY/RS"})
    void testVerifyWithKnownHashesExpectMatch(String password, String expectedHash) {
        assert(Hasher.verify(password, expectedHash));
    }

    @ParameterizedTest
    @CsvSource({
      "PasswordA", "Password123", "Password123!@#$%^&*", "Pass word A 1 !"})
    void testHashPasswordWithKnownHashesExpectMatch(String password) {
        String expectedHash = Hasher.hashPassword(password);
        assert(Hasher.verify(password, expectedHash));
    }

}
