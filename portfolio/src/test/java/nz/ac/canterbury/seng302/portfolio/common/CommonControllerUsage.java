package nz.ac.canterbury.seng302.portfolio.common;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.time.Instant;

/**
 * Responsible for having common usage attributes for the controller tests
 */
public class CommonControllerUsage {

    public static final AuthState validAuthStateAdmin = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
        .addClaims(ClaimDTO.newBuilder().setType("unique_name").setValue("Timmy Little").build()) // Set the mock user's username
        .addClaims(ClaimDTO.newBuilder().setType("name").setValue("Ya Boi").build()) // Set the mock user's name
            .addClaims(ClaimDTO.newBuilder().setType("exp").setValue(String.valueOf(Instant.now().getEpochSecond() + 864000)).build()) // Set the expiry time
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    public static final AuthState validAuthStateTeacher = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("TEACHER").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
        .addClaims(ClaimDTO.newBuilder().setType("unique_name").setValue("Timmy Little").build()) // Set the mock user's username
        .addClaims(ClaimDTO.newBuilder().setType("name").setValue("Ya Boi").build()) // Set the mock user's name
        .addClaims(ClaimDTO.newBuilder().setType("exp").setValue(String.valueOf(Instant.now().getEpochSecond() + 864000)).build()) // Set the expiry time
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    public static final AuthState validAuthStateStudent = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("STUDENT").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
        .addClaims(ClaimDTO.newBuilder().setType("unique_name").setValue("Timmy Little").build()) // Set the mock user's username
        .addClaims(ClaimDTO.newBuilder().setType("name").setValue("Ya Boi").build()) // Set the mock user's name
        .addClaims(ClaimDTO.newBuilder().setType("exp").setValue(String.valueOf(Instant.now().getEpochSecond() + 864000)).build()) // Set the expiry time
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    public static final AuthState invalidAuthState = AuthState.newBuilder()
            .setIsAuthenticated(false)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .addClaims(ClaimDTO.newBuilder().setType("role").setValue("ADMIN").build()) // Set the mock user's role
            .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue("123456").build()) // Set the mock user's ID
        .addClaims(ClaimDTO.newBuilder().setType("unique_name").setValue("Timmy Little").build()) // Set the mock user's username
        .addClaims(ClaimDTO.newBuilder().setType("name").setValue("Ya Boi").build()) // Set the mock user's name
        .addClaims(ClaimDTO.newBuilder().setType("exp").setValue(String.valueOf(Instant.now().getEpochSecond() + 864000)).build()) // Set the expiry time
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("")
            .build();


    public static final UserResponse testUserTeacher = UserResponse.newBuilder()
            .setBio("testbio")
            .setCreated(Timestamp.newBuilder().setSeconds(10))
            .setEmail("test@email")
            .setFirstName("testfirstname")
            .setLastName("testlastname")
            .setMiddleName("testmiddlename")
            .setNickname("testnickname")
            .setPersonalPronouns("test/test")
            .addRoles(UserRole.TEACHER)
            .build();

    public static final UserResponse testUserAdmin = UserResponse.newBuilder()
            .setBio("testbio")
            .setCreated(Timestamp.newBuilder().setSeconds(10))
            .setEmail("test@email")
            .setFirstName("testfirstname")
            .setLastName("testlastname")
            .setMiddleName("testmiddlename")
            .setNickname("testnickname")
            .setPersonalPronouns("test/test")
            .addRoles(UserRole.COURSE_ADMINISTRATOR)
            .build();

    public static final UserResponse testUserStudent = UserResponse.newBuilder()
            .setBio("testbio")
            .setCreated(Timestamp.newBuilder().setSeconds(10))
            .setEmail("test@email")
            .setFirstName("testfirstname")
            .setLastName("testlastname")
            .setMiddleName("testmiddlename")
            .setNickname("testnickname")
            .setPersonalPronouns("test/test")
            .addRoles(UserRole.STUDENT)
            .build();

    public static final UserResponse testInvalidUser= UserResponse.newBuilder()
            .setBio("testbio")
            .setCreated(Timestamp.newBuilder().setSeconds(10))
            .setEmail("test@email")
            .setFirstName("testfirstname")
            .setLastName("testlastname")
            .setMiddleName("testmiddlename")
            .setNickname("testnickname")
            .setPersonalPronouns("test/test")
            .addRoles(UserRole.STUDENT)
            .build();
}
