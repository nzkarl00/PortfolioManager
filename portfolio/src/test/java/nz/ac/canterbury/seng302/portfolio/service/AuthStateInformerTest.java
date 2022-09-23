package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AuthStateInformerTest {

    @Test
    void getRoleTest() {
        String expected = "teacher";
        ClaimDTO claimDTO = ClaimDTO.newBuilder().setType("role").setValue(expected).build();
        AuthState authState = AuthState.newBuilder().addClaims(claimDTO).build();
        assertEquals(expected, AuthStateInformer.getRole(authState));

        expected = "student";
        claimDTO = ClaimDTO.newBuilder().setType("role").setValue(expected).build();
        authState = AuthState.newBuilder().addClaims(claimDTO).build();
        assertEquals(expected, AuthStateInformer.getRole(authState));

        expected = "course_admin";
        claimDTO = ClaimDTO.newBuilder().setType("role").setValue(expected).build();
        authState = AuthState.newBuilder().addClaims(claimDTO).build();
        assertEquals(expected, AuthStateInformer.getRole(authState));

        claimDTO = ClaimDTO.newBuilder().setType("role").setValue("fail").build();
        authState = AuthState.newBuilder().addClaims(claimDTO).build();
        assertNotEquals(expected, AuthStateInformer.getRole(authState));
    }

    @Test
    void getIdTest() {
        String expected = "1";
        ClaimDTO claimDTO = ClaimDTO.newBuilder().setType("nameid").setValue(expected).build();
        AuthState authState = AuthState.newBuilder().addClaims(claimDTO).build();
        assertEquals(Integer.parseInt(expected), AuthStateInformer.getId(authState));

        claimDTO = ClaimDTO.newBuilder().setType("nameid").setValue("fail").build();
        authState = AuthState.newBuilder().addClaims(claimDTO).build();
        assertNotEquals(expected, AuthStateInformer.getRole(authState));
    }

    @Test
    void getUsernameTest() {
        String expected = "yaboi";
        ClaimDTO claimDTO = ClaimDTO.newBuilder().setType("unique_name").setValue(expected).build();
        AuthState authState = AuthState.newBuilder().addClaims(claimDTO).build();
        assertEquals(expected, AuthStateInformer.getUsername(authState));

        claimDTO = ClaimDTO.newBuilder().setType("unique_name").setValue("fail").build();
        authState = AuthState.newBuilder().addClaims(claimDTO).build();
        assertNotEquals(expected, AuthStateInformer.getUsername(authState));
    }
}