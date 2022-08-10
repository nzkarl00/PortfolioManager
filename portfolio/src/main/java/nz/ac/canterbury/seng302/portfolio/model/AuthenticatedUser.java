package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import java.time.Instant;
import java.util.Optional;

public class AuthenticatedUser {
    private int id = -1;
    private String role;
    private String username;
    private String name;

    /**
     * Construct an authenticated user from the AuthState
     * @param authState
     */
    public AuthenticatedUser(AuthState authState) {
        // Check their token has not expired
        Optional<String> expiryStr = authState.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("exp"))
                .findFirst()
                .map(ClaimDTO::getValue);
        if (expiryStr.isEmpty()) {
            throw new IllegalArgumentException("AuthState must contain an expiry claim");
        }

        // Validate the expiry has not occurred
        int expiry = Integer.parseInt(expiryStr.get());
        if (expiryHasOccurred(expiry)) {
            throw new IllegalArgumentException("AuthState has expired, re-authentication required");
        }

        // Extract the claims
        for (ClaimDTO claim : authState.getClaimsList()) {
            if (claim.getType().equals("role")) {
                role = claim.getValue();
            } else if (claim.getType().equals("nameid")) {
                id = Integer.parseInt(claim.getValue());
            } else if (claim.getType().equals("unique_name")) {
                username = claim.getValue();
            } else if (claim.getType().equals("name")) {
                name = claim.getValue();
            }
        }

        // Throw an error if the authenticated user doesn't have the required properties
        if (role == null || id == -1 || username == null || name == null) {
            throw new IllegalArgumentException("AuthState must contain all required claims");
        }
    }

    /**
     * Check that the JWT expiry has not yet occurred
     * @param expiry seconds since UTC epoch
     * @return true if the expiry has occurred
     */
    private boolean expiryHasOccurred(int expiry) {
        long now = Instant.now().getEpochSecond();
        return expiry <= now;
    }

    /**
     * Get the extracted user ID
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Get the extracted user role, uses highest role
     * @return
     */
    public String getRole() {
        return role;
    }

    /**
     * Get the extracted username
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the extracted name
     * @return
     */
    public String getName() {
        return name;
    }
}
