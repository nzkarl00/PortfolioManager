package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceUserRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.LinkedCommitRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLinkRepository;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.*;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepo;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EditEvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EditEvidenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    NavController navController;

    @MockBean
    EvidenceService evidenceService;

    @MockBean
    EvidenceRepository evidenceRepository;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    EvidenceUserRepository evidenceUserRepository;

    @MockBean
    WebLinkRepository webLinkRepository;

    @MockBean
    GroupsClientService groupsClientService;

    @MockBean
    LinkedCommitRepository linkedCommitRepository;

    @Before
    void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(CommitSearchController.class)
                .build();
    }

    @Test
    // Currently, for these tests, only the Skills parameters matter
    // Checks that skills fields are accepted with multiple entries
    void validateEditEvidenceParameters_allValidAndMultiple() {
        Assertions.assertDoesNotThrow(() -> EditEvidenceController.validateEditEvidenceParameters(
                1,
                "",
                "",
                1,
                "",
                "5 6",
                "8:new_name 10:hello_there",
                "new_skill another_skill",
                "",
                ""
        ));
    }

    // Currently, for these tests, only the Skills parameters matter
    // Checks that skills fields are accepted with single entries
    void validateEditEvidenceParameters_allValidAndSingular() {
        Assertions.assertDoesNotThrow(() -> EditEvidenceController.validateEditEvidenceParameters(
                1,
                "",
                "",
                1,
                "",
                "6",
                "8:new_name",
                "new_skill",
                "",
                ""
        ));
    }

    void validateEditEvidenceParameters_acceptsForeignLanguageCharacters() {
        Assertions.assertDoesNotThrow(() -> EditEvidenceController.validateEditEvidenceParameters(
                1,
                "",
                "",
                1,
                "",
                "6",
                "8:Ф",
                "Ф",
                "",
                ""
        ));
    }

    @Test
    void validateEditEvidenceParameters_allSkillsEmpty() {
        Assertions.assertDoesNotThrow(() -> EditEvidenceController.validateEditEvidenceParameters(
                1,
                "",
                "",
                1,
                "",
                "",
                "",
                "",
                "",
                ""
        ));
    }

    @Test
    void validateEditEvidenceParameters_rejectsInvalidDelete() {
        Exception res = Assertions.assertThrows(IllegalArgumentException.class,
                () -> EditEvidenceController.validateEditEvidenceParameters(
                1,
                "",
                "",
                1,
                "",
                "-1",
                "",
                "",
                "",
                ""
        ));
        Assertions.assertEquals("Skills to delete must be a sentence of numbers", res.getMessage());
    }

    @Test
    void validateEditEvidenceParameters_rejectsInvalidEdit() {
        Exception res = Assertions.assertThrows(IllegalArgumentException.class,
                () -> EditEvidenceController.validateEditEvidenceParameters(
                1,
                "",
                "",
                1,
                "",
                "",
                ":new_name",
                "",
                "",
                ""
        ));
        Assertions.assertEquals("Skills to edit is of incorrect form", res.getMessage());
    }

}
