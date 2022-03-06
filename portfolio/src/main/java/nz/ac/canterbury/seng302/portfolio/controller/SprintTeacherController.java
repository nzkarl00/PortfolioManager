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
public class SprintTeacherController {

  @GetMapping("/sprintTeacher")
  public String sprintTeacher(
      @AuthenticationPrincipal AuthState principal,
      @RequestParam(name="name", required=false, defaultValue="Sprint 1") String editName,
      @RequestParam(name="description", required=false, defaultValue="This is the first sprint and we " +
          "intend to lorem ipsum dolor sit amet,\n" +
          "        consectetur adipiscing elit. Proin eu libero ultrices, luctus arcu nec,\n" +
          "        ullamcorper purus. Nam arcu libero, tincidunt nec augue quis, egestas\n" +
          "        iaculis mauris. Curabitur auctor nisi lectus, a ultrices tellus ultricies.")
          String editDescription,
      @RequestParam(name="start", required=false, defaultValue="28 Feb 2022") String editStart,
      @RequestParam(name="end", required=false, defaultValue="7 Mar 2022") String editEnd,
      Model model
  ) {
    String role = principal.getClaimsList().stream()
        .filter(claim -> claim.getType().equals("role"))
        .findFirst()
        .map(ClaimDTO::getValue)
        .orElse("NOT FOUND");

    if (role == "teacher") {
      model.addAttribute("currentName", editName);
      model.addAttribute("currentDescription", editDescription);
      model.addAttribute("currentStart", editStart);
      model.addAttribute("currentEnd", editEnd);
      return "sprintTeacher";
    } else {
      return "redirect:/error";
    }
  }

  @PostMapping("/editName")
  public String editName(
      @AuthenticationPrincipal AuthState principal,
      @RequestParam(value="editName") String editName,
      Model model
  ) {
    return "redirect:/sprintTeacher?name=" + editName;
  }

  @PostMapping("/editDescription")
  public String editDescription(
      @AuthenticationPrincipal AuthState principal,
      @RequestParam(value="editDescription") String editDescription,
      Model model
  ) {
    return "redirect:/sprintTeacher?description=" + editDescription;
  }

  @PostMapping("/editStart")
  public String editStart(
      @AuthenticationPrincipal AuthState principal,
      @RequestParam(value="editStart") String editStart,
      Model model
  ) {
    return "redirect:/sprintTeacher?start=" + editStart;
  }

  @PostMapping("/editEnd")
  public String editEnd(
      @AuthenticationPrincipal AuthState principal,
      @RequestParam(value="editEnd") String editEnd,
      Model model
  ) {
    return "redirect:/sprintTeacher?end=" + editEnd;
  }

}
