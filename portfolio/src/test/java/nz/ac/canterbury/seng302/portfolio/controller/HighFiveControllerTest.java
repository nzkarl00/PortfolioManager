package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.HighFive;
import nz.ac.canterbury.seng302.portfolio.model.evidence.HighFiveRepository;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateTeacher;
import static nz.ac.canterbury.seng302.portfolio.common.CommonProjectItems.getValidProject;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HighFiveController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HighFiveControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HighFiveRepository highFiveRepository;
    @MockBean
    private EvidenceRepository evidenceRepository;
    @MockBean
    private AccountClientService accountClientService;

    static MockedStatic<AuthStateInformer> utilities;

    private static final Project testProject = getValidProject();
    private static final LocalDate may4 = LocalDate.parse("2022-05-04");
    private static final Evidence testEvidence = new Evidence(
            123456,
            testProject,
            "Evidence One",
            "This evidence is the first to be submitted",
            may4,
            Evidence.SERVICE
    );

    @BeforeAll
    public static void open() {
        utilities = Mockito.mockStatic(AuthStateInformer.class);
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }

    @Test
    public void postHighFive_OnValidEvidence() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        when(evidenceRepository.findById(123456)).thenReturn(testEvidence);
        when(accountClientService.getUserById(1)).thenReturn(UserResponse.newBuilder().setFirstName("admin").setLastName("admin").build());
        when(highFiveRepository.findByParentEvidenceAndParentUserId(testEvidence, 1)).thenReturn(null);
        MvcResult result = mockMvc.perform(post("/high-five").param("evidenceId", String.valueOf(123456)))
                .andExpect(status().isOk()).andReturn();
        // Verifies response is "added"
        Assertions.assertEquals("added", result.getResponse().getContentAsString());
        // Verifies high five was saved
        verify(highFiveRepository).save(any(HighFive.class));
    }

    @Test
    public void postHighFive_OnValidEvidence_WithExistingHighFive() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        Evidence mockEvidence = Mockito.mock(Evidence.class);
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        when(evidenceRepository.findById(123456)).thenReturn(mockEvidence);
        when(accountClientService.getUserById(1)).thenReturn(UserResponse.newBuilder().setFirstName("admin").setLastName("admin").build());
        doNothing().when(mockEvidence).removeHighFive(any());
        // High five exists
        when(highFiveRepository.findByParentEvidenceAndParentUserId(mockEvidence, 1)).thenReturn(new HighFive());
        MvcResult result = mockMvc.perform(post("/high-five").param("evidenceId", String.valueOf(123456)))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertEquals("deleted", result.getResponse().getContentAsString());
        // Verifies high five was not saved
        verify(highFiveRepository, never()).save(any(HighFive.class));
        verify(highFiveRepository, atMostOnce()).delete(any((HighFive.class)));
    }

    @Test
    public void postHighFive_OnInvalidEvidence() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        when(evidenceRepository.findById(123456)).thenReturn(null);
        when(accountClientService.getUserById(1)).thenReturn(UserResponse.newBuilder().setFirstName("admin").setLastName("admin").build());
        when(highFiveRepository.findByParentEvidenceAndParentUserId(testEvidence, 1)).thenReturn(null);
        MvcResult result = mockMvc.perform(post("/high-five").param("evidenceId", String.valueOf(123456)))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertEquals("error", result.getResponse().getContentAsString());
        // Verifies high five was not saved
        verify(highFiveRepository, never()).save(any(HighFive.class));
    }
}
