package nz.ac.canterbury.seng302.portfolio.service;

import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Commit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class GitlabClientTest {
    @Spy
    GitLabApi api = Mockito.spy(new GitLabApi("", ""));
    @Spy
    CommitsApi commitsApi = Mockito.spy(new CommitsApi(api));
    @InjectMocks
    GitlabClient client = new GitlabClient();

    @BeforeAll
    static void beforeEach() {

    }

    @Test
    void genRepoName_joinsStrings() {
        String result = GitlabClient.genRepoName("owner", "name");
        Assertions.assertEquals("owner/name", result);
    }

    @Test
    void getCommits_onePage() throws Exception {
        // Expected commits list
        List<Commit> commitList = List.of(new Commit());
        Pager mockPager = Mockito.mock(Pager.class);
        Mockito.when(mockPager.current()).thenReturn(commitList);

        Mockito.doReturn(commitsApi).when(api).getCommitsApi();
        Mockito.doReturn(mockPager).when(commitsApi).getCommits(anyString(), anyInt());

        Assertions.assertDoesNotThrow(() -> {
            List<Commit> commits = client.getCommits(api, "a", "b", 10);
            Assertions.assertEquals(commitList, commits);
        });
    }

    @Test
    void getCommits_throwOnGetCommits() throws Exception {
        // Expected commits list
        List<Commit> commitList = List.of(new Commit());
        Pager mockPager = Mockito.mock(Pager.class);
        Mockito.when(mockPager.current()).thenReturn(commitList);


        Mockito.doReturn(commitsApi).when(api).getCommitsApi();
        Mockito.doThrow(new GitLabApiException("Fake error")).when(commitsApi).getCommits(anyString(), anyInt());

        Assertions.assertThrows(GitLabApiException.class, () -> {
            List<Commit> commits = client.getCommits(api, "a", "b", 10);
            Assertions.assertEquals(commitList, commits);
        });
    }

    @Test
    void getCommitsInDateRange_onePage() throws Exception {
        // Expected commits list
        List<Commit> commitList = List.of(new Commit());
        Pager mockPager = Mockito.mock(Pager.class);
        Mockito.when(mockPager.current()).thenReturn(commitList);

        Mockito.doReturn(commitsApi).when(api).getCommitsApi();
        Mockito.doReturn(mockPager).when(commitsApi).getCommits(
                anyString(), anyString(), any(), any(), anyInt()
        );

        Assertions.assertDoesNotThrow(() -> {
            List<Commit> commits = client.getCommitsInDateRange(api, "a", "b", 10, null, null);
            Assertions.assertEquals(commitList, commits);
        });
    }

    @Test
    void getCommitsInDateRange_throwOnGetCommits() throws Exception {
        // Expected commits list
        List<Commit> commitList = List.of(new Commit());
        Pager mockPager = Mockito.mock(Pager.class);
        Mockito.when(mockPager.current()).thenReturn(commitList);

        Mockito.doReturn(commitsApi).when(api).getCommitsApi();
        Mockito.doThrow(new GitLabApiException("Fake error")).when(commitsApi).getCommits(
                anyString(), anyString(), any(), any(), anyInt()
        );

        Assertions.assertThrows(GitLabApiException.class, () -> {
            List<Commit> commits = client.getCommitsInDateRange(api, "a", "b", 10, null, null);
            Assertions.assertEquals(commitList, commits);
        });
    }
}
