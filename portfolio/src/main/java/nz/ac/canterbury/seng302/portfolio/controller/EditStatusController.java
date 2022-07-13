package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateSocketService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class EditStatusController {

    @Autowired
    DateSocketService dateSocketService;

    @Autowired
    ProjectService projectService;

    public class BodyType {

        public BodyType() {}

        private Integer projectId;
        private String type;
    }

    @PostMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public void editStatus(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "type") String type
    ) throws Exception {
        dateSocketService.sendEditStatus(projectService.getProjectById(projectId), AuthStateInformer.getUsername(principal) + " is editing a " + type);
    }

}
