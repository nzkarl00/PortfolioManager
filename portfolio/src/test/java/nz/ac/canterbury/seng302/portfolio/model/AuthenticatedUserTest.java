package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;


public class AuthenticatedUserTest {
    static AuthState authState;
    static AuthState authStateExpired;
    static AuthState authStateMissingClaim;

    @BeforeAll
    static void beforeAll() {
        // Setup the required claims
        ClaimDTO roleDTO = ClaimDTO.newBuilder().setType("role").setValue("teacher").build();
        ClaimDTO idDTO = ClaimDTO.newBuilder().setType("nameid").setValue("1").build();
        ClaimDTO nameDTO = ClaimDTO.newBuilder().setType("name").setValue("The Teacher").build();
        ClaimDTO uniqueNameDTO = ClaimDTO.newBuilder().setType("unique_name").setValue("teacher_username").build();
        ClaimDTO expiredDTO = ClaimDTO.newBuilder().setType("exp").setValue(
                String.format("%d", (Instant.now().getEpochSecond() - 100000))
        ).build();
        ClaimDTO unexpiredDTO = ClaimDTO.newBuilder().setType("exp").setValue(
                String.format("%d", (Instant.now().getEpochSecond() + 100000))
        ).build();

        authState = AuthState.newBuilder()
                .addClaims(roleDTO)
                .addClaims(idDTO)
                .addClaims(nameDTO)
                .addClaims(uniqueNameDTO)
                .addClaims(unexpiredDTO) // unexpired
                .build();

        authStateExpired = AuthState.newBuilder()
                .addClaims(roleDTO)
                .addClaims(idDTO)
                .addClaims(nameDTO)
                .addClaims(uniqueNameDTO)
                .addClaims(expiredDTO) // expired
                .build();

        authStateMissingClaim = AuthState.newBuilder()
                .addClaims(idDTO) // missing role
                .addClaims(nameDTO)
                .addClaims(uniqueNameDTO)
                .addClaims(unexpiredDTO) // unexpired
                .build();
    }

    @Test
    void constructor_validAuthState() {
        Assertions.assertDoesNotThrow(() -> {
            new AuthenticatedUser(authState);
        });
    }

    @Test
    void constructor_expiredAuthState() {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new AuthenticatedUser(authStateExpired);
        });
        Assertions.assertEquals("AuthState has expired, re-authentication required", exception.getMessage());
    }

    @Test
    void constructor_authStateMissingCLaim() {
        Throwable exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new AuthenticatedUser(authStateMissingClaim);
        });
        Assertions.assertEquals("AuthState must contain all required claims", exception.getMessage());
    }

    @Test
    void getClaims() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(authState);
        Assertions.assertEquals("teacher", authenticatedUser.getRole());
        Assertions.assertEquals(1, authenticatedUser.getId());
        Assertions.assertEquals("The Teacher", authenticatedUser.getName());
        Assertions.assertEquals("teacher_username", authenticatedUser.getUsername());
    }
}
