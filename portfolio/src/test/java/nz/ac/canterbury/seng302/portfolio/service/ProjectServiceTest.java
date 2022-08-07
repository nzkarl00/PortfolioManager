package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {
    @Mock
    ProjectRepository repository;

    @InjectMocks
    ProjectService projectService = new ProjectService();

    static LocalDate currentDate = LocalDate.now();

    static Project testProject1 = new Project("Project Viper", "This project is for researching vipers", currentDate.plusMonths(5), currentDate.plusMonths(10));
    static Project testProject2 = new Project("Project Owl", "This project is for researching owl", currentDate, currentDate.plusMonths(3));
    static List<Project> testProjectList = new ArrayList<>();


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work
        testProjectList.add(testProject1);
        testProjectList.add(testProject2);
    }

    @Test
    void getAllProjects() throws ParseException {
        when(repository.findAll()).thenReturn(testProjectList);
        List<Project> result = projectService.getAllProjects();
        assertEquals(testProjectList, result);
    }

    @Test
    void getProjectById_NoValidProjectWithId() throws Exception {
        Mockito.when(repository.findById(619)).thenReturn(null);
        Exception exception = assertThrows(Exception.class, () -> {
            projectService.getProjectById(619);;
        });
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void createDefaultProject() throws Exception {
        projectService.createDefaultProject();
        verify(repository, times(1)).save(any(Project.class));
    }

}
