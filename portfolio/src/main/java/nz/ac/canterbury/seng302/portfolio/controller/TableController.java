package nz.ac.canterbury.seng302.portfolio.controller;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

@Controller
public class TableController {

    @Autowired
    private AccountClientService accountClientService;

    private int start = 0;
    private int step = 50;
    private int currentPage = 0;
    String sortMode = "first_name";
    Integer ascDesc = 0;
    Boolean isSorted = false;
    List<User> users = new ArrayList<User>();

    String firstNameShow = "background-color:#056BFA !important;";
    String lastNameShow = "";
    String usernameShow = "";
    String nicknameShow = "";
    String rolesShow = "";

    String firstNameUp = "";
    String firstNameDown = "display:none;";
    String lastNameUp = "display:none;";
    String lastNameDown = "display:none;";
    String usernameUp = "display:none;";
    String usernameDown = "display:none;";
    String nicknameUp = "display:none;";
    String nicknameDown = "display:none;";
    String rolesUp = "display:none;";
    String rolesDown = "display:none;";


    /**
     * The controller mapping of the User table
     * @param principal the auth token
     * @param model the model add templating attributes to
     * @param move param to move back or forward a page
     * @return String corresponding to html page
     */
    @GetMapping("/user-list")
    public String account(
        @AuthenticationPrincipal AuthState principal,
        Model model,
        @RequestParam("move") Optional<String> move) {
        // Note the pagination was taken from https://www.baeldung.com/spring-thymeleaf-pagination
        // Update, a bit of philosophy from the above link
        // and some details from https://stackoverflow.com/questions/5095887/how-do-i-pass-a-url-with-multiple-parameters-into-a-url
        // and https://stackoverflow.com/questions/46216134/thymeleaf-how-to-make-a-button-link-to-another-html-page
        String movePage = move.orElse("");

        // Receive the forward or backward call from the button and iterate current page
        if (movePage.equals("forward")) {
            currentPage++;
        } else if (movePage.equals("back")) {
            if (currentPage > 0) {
                currentPage--;
            }
        }
        start = currentPage * step;

        users.clear();
        isSorted = false;
        Integer id = AuthStateInformer.getId(principal);

        UserResponse userReply;
        userReply = accountClientService.getUserById(id); // Get the user

        model.addAttribute("date", DateParser.displayDate(userReply));
        model.addAttribute("start", start);
        model.addAttribute("currentPage", currentPage);

        model.addAttribute("firstNameShow", firstNameShow);
        model.addAttribute("lastNameShow", lastNameShow);
        model.addAttribute("usernameShow", usernameShow);
        model.addAttribute("nicknameShow", nicknameShow);
        model.addAttribute("rolesShow", rolesShow);

        model.addAttribute("firstNameUp", firstNameUp);
        model.addAttribute("firstNameDown", firstNameDown);
        model.addAttribute("lastNameUp", lastNameUp);
        model.addAttribute("lastNameDown", lastNameDown);
        model.addAttribute("usernameUp", usernameUp);
        model.addAttribute("usernameDown", usernameDown);
        model.addAttribute("nicknameUp", nicknameUp);
        model.addAttribute("nicknameDown", nicknameDown);
        model.addAttribute("rolesUp", rolesUp);
        model.addAttribute("rolesDown", rolesDown);

        PaginatedUsersResponse response = accountClientService.getPaginatedUsers(step, start, sortMode, ascDesc);
        List<User> users = new ArrayList<>();
        for (UserResponse userResponse : response.getUsersList()) { // loop through the given response containing UserResponses
            users.add(new User(userResponse)); // pass the UserResponse to the User constructor which builds the appropriate user
        }
        model.addAttribute("users", users);
        return "userList";
    }


    @PostMapping("order-list")
    public String sprintDelete(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="sortColumn") String sortColumn,
            Model model
    ) throws Exception {


        String isUp = "";
        String isDown = "";

        if (sortColumn.equals(sortMode)) {
            if (ascDesc == 1) {
                ascDesc = 0;
                isUp = "";
                isDown = "display:none;";
            } else {
                ascDesc = 1;
                isDown = "";
                isUp = "display:none;";
            }
        } else {
            sortMode = sortColumn;
            ascDesc = 0;
            isUp = "";
            isDown = "display:none;";
        }

        String tempValue = "background-color:#056BFA !important;";

        firstNameShow = "";
        lastNameShow = "";
        usernameShow = "";
        nicknameShow = "";
        rolesShow = "";

        firstNameUp = "display:none;";
        firstNameDown = "display:none;";
        lastNameUp = "display:none;";
        lastNameDown = "display:none;";
        usernameUp = "display:none;";
        usernameDown = "display:none;";
        nicknameUp = "display:none;";
        nicknameDown = "display:none;";
        rolesUp = "display:none;";
        rolesDown = "display:none;";

        if (sortColumn.equals("roles")) {
            rolesShow = tempValue;
            rolesUp = isUp;
            rolesDown = isDown;
        } else if (sortColumn.equals("nickname")) {
            nicknameShow = tempValue;
            nicknameUp = isUp;
            nicknameDown = isDown;
        } else if (sortColumn.equals("username")) {
            usernameShow = tempValue;
            usernameUp = isUp;
            usernameDown = isDown;
        } else if (sortColumn.equals("last_name")) {
            lastNameShow = tempValue;
            lastNameUp = isUp;
            lastNameDown = isDown;
        } else if (sortColumn.equals("first_name")) {
            firstNameShow = tempValue;
            firstNameUp = isUp;
            firstNameDown = isDown;
        }

            return "redirect:/user-list";
    }
}