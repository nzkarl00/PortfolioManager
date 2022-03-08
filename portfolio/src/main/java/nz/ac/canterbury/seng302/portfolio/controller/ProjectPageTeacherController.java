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

import java.sql.Timestamp;

@Controller
public class ProjectPageTeacherController {

    @GetMapping("/projectPageTeacher")
    public String projectPageTeacher(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="name", required= true, defaultValue="Default Project") String editName,
            @RequestParam(name="description", required=false, defaultValue=" A project description is a high-level overview of why you're" +
                    " doing a project. The document explains a project's objectives\n" +
                    "        and its essential qualities. Think of it as the elevator pitch that " +
                    "focuses on what and why without delving into how.")
                    String editDescription,
            @RequestParam(name="start", required=false, defaultValue="03 Mar 2022") String editStart,
            @RequestParam(name="end", required=false, defaultValue="10 Jun 2022") String editEnd,
            @RequestParam(name="status", required=false, defaultValue="404") String status,
            Model model
    ) {
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        if (role.equals("teacher")) {
            model.addAttribute("currentName", editName);
            model.addAttribute("currentDescription", editDescription);
            model.addAttribute("currentStart", editStart);
            model.addAttribute("currentEnd", editEnd);
            return "projectPageTeacher";
        } else {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            model.addAttribute("timestamp", time);
            model.addAttribute("path", "/projectPageTeacher");
            model.addAttribute("error", "Forbidden");
            model.addAttribute("status", "404");
            model.addAttribute("message", "Access Denied");
            return "/error";
        }
    }

    @PostMapping("/editName")
    public String editName(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="editName") String editName,
            Model model
    ) {
        return "redirect:/projectPageTeacher?name=" + editName;
    }

    @PostMapping("/editDescription")
    public String editDescription(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="editDescription") String editDescription,
            Model model
    ) {
        return "redirect:/projectPageTeacher?description=" + editDescription;
    }

    @PostMapping("/editStart")
    public String editStart(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="editStart") String editStart,
            Model model
    ) {
        return "redirect:/projectPageTeacher?start=" + editStart;
    }

    @PostMapping("/editEnd")
    public String editEnd(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="editEnd") String editEnd,
            Model model
    ) {
        return "redirect:/projectPageTeacher?end=" + editEnd;
    }

}