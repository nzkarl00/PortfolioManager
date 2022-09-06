package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

/**
 * Cleaning up the codebase by putting these methods in a service class
 * that can return these values when needed
 */
public class AuthStateInformer {

    /**
     * return the role of the auth token
     * @param principal the auth token
     * @return the role
     */
    public static String getRole(AuthState principal) {
        return principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("role"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("NOT FOUND");
    }

    /**
     * return the id of the auth token
     * @param principal the auth token
     * @return the id
     */
    public static Integer getId(AuthState principal) {
        return Integer.valueOf(principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("nameid"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("-100"));
    }

    /**
     * return the username of the auth token
     * @param principal the auth token
     * @return the username
     */
    public static String getUsername(AuthState principal) {
        return principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("unique_name"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("-100");
    }
}
