package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateSocketService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class EditStatusController {

    @Autowired
    DateSocketService dateSocketService;

    @Autowired
    ProjectService projectService;

    @PostMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public void editStatus(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "type") String type
    ) throws Exception {
        dateSocketService.sendEditStatus(projectService.getProjectById(projectId), AuthStateInformer.getUsername(principal) + " is " + type);
    }

}
