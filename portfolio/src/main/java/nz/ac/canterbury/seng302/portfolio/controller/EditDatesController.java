package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditDatesController {

    @Autowired
    private ProjectRepository repository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private NavController navController;


    String errorShow = "display:none;";
    String errorCode = "";

    /**
     * Redirects top the edit project page
     *
     * @param model The model to be used by the application for web integration
     * @return the edit project page
     */
    @GetMapping("/edit-dates")
    public String projectForm(@AuthenticationPrincipal AuthState principal, @RequestParam(value = "id") Integer projectId, Model model) throws Exception {

        Project project = projectService.getProjectById(projectId);
        /* Add project details to the model */
        model.addAttribute("projectName", project.getName());
        model.addAttribute("project", project);
        model.addAttribute("projectStartDate", project.getStartDateStringHtml());
        model.addAttribute("projectEndDate", project.getEndDateStringHtml());
        model.addAttribute("projectDescription", project.getDescription());
        model.addAttribute("errorShow", errorShow);
        model.addAttribute("errorCode", errorCode);

        Integer id = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        navController.updateModelForNav(principal, model, userReply, id);
        // End of Attributes for header

        // Reset for the next display of the page
        errorShow = "display:none;";
        errorCode = "";

        // Below code is just begging to be added as a method somewhere...
        String role = AuthStateInformer.getRole(principal);

        if (role.equals("teacher") || role.equals("admin")) {
            return "editProject";
        } else {
            return "userProjectDetails";
        }
    }
}
