package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectPageStudentController {

    @GetMapping("/projectPageStudent")
    public String projectPageStudentController(
            @RequestParam(name="sprints", required = false, defaultValue="sprint 1, sprint 2") String[] sprintList,
            Model model
    ) {
        model.addAttribute("currentSprints", sprintList);
        return "projectPageStudent";
    }
}
