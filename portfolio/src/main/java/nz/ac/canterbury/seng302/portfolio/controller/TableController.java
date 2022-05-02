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
    String role;

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

        users.clear();
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

        role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        model.addAttribute("userRole", role);



        isSorted = false;
        Integer id = AuthStateInformer.getId(principal);

        UserResponse userReply;
        userReply = accountClientService.getUserById(id); // Get the user

        model.addAttribute("date", DateParser.displayDate(userReply));
        model.addAttribute("start", start);
        model.addAttribute("currentPage", currentPage);

        PaginatedUsersResponse response = accountClientService.getPaginatedUsers(step, start, sortMode, ascDesc);
        users = new ArrayList<>();
        for (UserResponse userResponse : response.getUsersList()) { // loop through the given response containing UserResponses
            users.add(new User(userResponse)); // pass the UserResponse to the User constructor which builds the appropriate user
        }
        model.addAttribute("users", users);
        return "userList";
    }


    @PostMapping("order-list")
    public String orderUsers(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="sortColumn") String sortColumn,
            Model model
    ) throws Exception {

        if (sortColumn.equals(sortMode)) {
            if (ascDesc == 1) {
                ascDesc = 0;
            } else {
                ascDesc = 1;
            }
        } else {
            sortMode = sortColumn;
            ascDesc = 0;
        }

        return "redirect:/user-list";
    }


    @PostMapping("add-role")
    public String roleAdd(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="roleAdd") String roleAdd,
            @RequestParam(value="username") String username,
            Model model
    ) throws Exception {
        if (role.equals("teacher")) {
            User user = null;
            System.out.println(users);
            for (User userTemp : users) {
                if (userTemp.getUsername().equals(username)) {
                    user = userTemp;
                }
            }
            // Begins the checks if a user is found
            if (user != null) {
                Integer userId = user.getId();
                if (!user.listRoles().contains(roleAdd)) {
                    // Performs deletion if it passes all checks
                    UserRoleChangeResponse response = accountClientService.addRole(roleAdd, userId);
                    if (response.getIsSuccess()) {
                        //successShow = true;
                        //successMessage =
                    } else {
                        //thing
                    }
                }

            }
        }

        return "redirect:/user-list";
    }

    /**
     * Will validate the role deletion request, then execute it if applicable
     * @param principal
     * @param roleDelete The name of the role to be deleted
     * @param username The (unique) username of the user that's being deleted
     * @param model
     * @return returns the page again
     * @throws Exception
     */
    @PostMapping("delete-role")
    public String roleDelete(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="roleDelete") String roleDelete,
            @RequestParam(value="username") String username,
            Model model
    ) throws Exception {

        /* Return the name of the Thymeleaf template */
        // detects the role of the current user and performs appropriate action
        if (role.equals("teacher")) {
            // Locates the appropriate user
            User user = null;
            for (User userTemp : users) {
                if (userTemp.getUsername().equals(username)) {
                    user = userTemp;
                }
            }
            // Begins the checks if a user is found
            if (user != null) {
                Integer userId = user.getId();
                if (user.listRoles().size() > 1) {
                    if (user.listRoles().contains(roleDelete)) {
                        // Performs deletion if it passes all checks
                        accountClientService.deleteRole(roleDelete, userId);
                    }
                }
            }
        }
        return "redirect:/user-list";
    }
}
