package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.HighFive;
import nz.ac.canterbury.seng302.portfolio.model.evidence.HighFiveRepository;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.validAuthStateTeacher;
import static nz.ac.canterbury.seng302.portfolio.common.CommonProjectItems.getValidProject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HighFiveController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HighFiveControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    HighFiveRepository highFiveRepository;
    @Autowired
    EvidenceRepository evidenceRepository;

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

    @Test
    public void postValidEvidence_WithMultipleSkillTags() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));
        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        utilities.when(() -> AuthStateInformer.getId(validAuthStateTeacher)).thenReturn(1);
        mockMvc.perform(post("/high-five").param("evidenceId", String.valueOf(123456)))
                .andExpect(status().isOk());
        // Verifies evidence was saved
        verify(highFiveRepository).save(any(HighFive.class));
    }
}
