package nz.ac.canterbury.seng302.identityprovider.authentication;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.Role;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationValidatorUtilTest {

    private static AccountProfile testAccountProfile = new AccountProfile("test username",
        "test hash", new Date(), "test bio", "test email",
        "test/photopath/", "firstname", "lastname", "pronouns");

    private static Role testRole = new Role(testAccountProfile, "1student");

    @Test
    void validateTokenForAuthState_blueSky() {
        testAccountProfile.addRoleTestingOnly(testRole);
        JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();
        String token = jwtTokenUtil.generateTokenForUser(testAccountProfile);
        AuthState authState = AuthenticationValidatorUtil.validateTokenForAuthState(token);
        assertEquals("student", authState.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("role"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("NOT FOUND"));
    }
}