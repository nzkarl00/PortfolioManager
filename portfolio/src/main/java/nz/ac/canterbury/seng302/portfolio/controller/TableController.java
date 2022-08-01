package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.userGroups.UserPreference;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.UserPreferenceRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import java.util.List;

/**
 * controller responsible for displaying and processing the user table
 */
@Controller
public class TableController {

    @Autowired
    private AccountClientService accountClientService;

    @Autowired
    private NavController navController;

    @Autowired
    private UserPreferenceRepository userPreferenceRepo;
    private int start = 0;
    private int step = 50;
    private int currentPage = 0;
    String sortMode = ""; // The column in the user table to sort, e.g. by last name, by alias etc.
    Integer ascDesc = 0;
    Boolean isSorted = false;
    List<User> users = new ArrayList<User>();
    String role;

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
        HttpServletRequest request,
        @AuthenticationPrincipal AuthState principal,
        Model model,
        @RequestParam("move") Optional<String> move) throws IOException {
        // Note the pagination was taken from https://www.baeldung.com/spring-thymeleaf-pagination
        // Update, a bit of philosophy from the above link
        // and some details from https://stackoverflow.com/questions/5095887/how-do-i-pass-a-url-with-multiple-parameters-into-a-url
        // and https://stackoverflow.com/questions/46216134/thymeleaf-how-to-make-a-button-link-to-another-html-page

        String movePage = move.orElse("");

        // Receive the forward or backward call from the button and iterate current page
        if (movePage.equals("forward")) {
            if ((users.size() == 50)) { currentPage++; }
        } else if (movePage.equals("back")) {
            if (currentPage > 0) { currentPage--; }
        }

        users.clear();

        start = currentPage * step;

        role = AuthStateInformer.getRole(principal);

        model.addAttribute("userRole", role);
        step = 50;

        isSorted = false;
        Integer id = AuthStateInformer.getId(principal);

        UserResponse userReply;
        userReply = accountClientService.getUserById(id); // Get the user

        // update the sorting variables if there is a userPreference in the database to do so
        Optional<UserPreference> preferenceOptional = userPreferenceRepo.findById(id);
        UserPreference preference = preferenceOptional.orElse(null);
        if (preference != null) {
            ascDesc = preference.getSortOrder() == 1 ? 0 : 1;
            columnHeaderHelper(preference.getSortCol());
            sortMode = preference.getSortCol();
            ascDesc = preference.getSortOrder();
        }

        navController.updateModelForNav(principal, model, userReply, id);
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
        users = new ArrayList<>();
        for (UserResponse userResponse : response.getUsersList()) { // loop through the given response containing UserResponses
            users.add(new User(userResponse)); // pass the UserResponse to the User constructor which builds the appropriate user
        }
        model.addAttribute("users", users);
        return "userList";
    }


    /**
     * Processes the sortColumn request into a sorting mode and column for the controller to call from
     * @param principal auth token
     * @param sortColumn the column to sort by
     * @param model the model to add attributes to, to template from
     * @return redirection
     * @throws Exception
     */
    @PostMapping("order-list")
    public String orderUsers(
        HttpServletRequest request,
        HttpServletResponse response,
        @AuthenticationPrincipal AuthState principal,
        @RequestParam(value="sortColumn") String sortColumn,
        Model model
    ) throws Exception {
        int id = AuthStateInformer.getId(principal);

        UserPreference preference = userPreferenceRepo.findById(id);

        columnHeaderHelper(sortColumn);

        String sortAll = sortMode;
        if (ascDesc == 0) {
            sortAll += "_asc";
        } else {
            sortAll += "_dsc";
        }

        if (preference == null) {
            preference = new UserPreference(id, sortMode, ascDesc);
        } else {
            preference.setSortCol(sortMode);
            preference.setSortOrder(ascDesc);
        }
        userPreferenceRepo.save(preference);

        return "redirect:user-list";
    }

    private void columnHeaderHelper(String sortString) {
        String isUp = "";
        String isDown = "";

        // if it is currently sorting the column specified, switch the direction of sorting
        if (sortMode.equals(sortString)) {
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
            sortMode = sortString;
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

        if (sortString.equals("roles")) {
            rolesShow = tempValue;
            rolesUp = isUp;
            rolesDown = isDown;
        } else if (sortString.equals("nickname")) {
            nicknameShow = tempValue;
            nicknameUp = isUp;
            nicknameDown = isDown;
        } else if (sortString.equals("username")) {
            usernameShow = tempValue;
            usernameUp = isUp;
            usernameDown = isDown;
        } else if (sortString.equals("last_name")) {
            lastNameShow = tempValue;
            lastNameUp = isUp;
            lastNameDown = isDown;
        } else if (sortString.equals("first_name")) {
            firstNameShow = tempValue;
            firstNameUp = isUp;
            firstNameDown = isDown;
        }
    }

    @PostMapping("add-role")
    public String roleAdd(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="roleAdd") String roleAdd,
            @RequestParam(value="username") String username,
            Model model
    ) throws Exception {
        if (role.equals("teacher") || role.equals("admin")) {
            User user = null;
            for (User userTemp : users) {
                if (userTemp.getUsername().equals(username)) {
                    user = userTemp;
                }
            }
            // Begins the checks if a user is found
            if (user != null) {
                Integer userId = user.getId();
                if (!user.listRoles().contains(roleAdd)) {
                    if (roleAdd.equals("admin")) {
                        roleAdd = "course_administrator";
                    }
                    // Performs deletion if it passes all checks
                    UserRoleChangeResponse response = accountClientService.addRole(roleAdd, userId);
                    if (response.getIsSuccess()) {
                        return "redirect:user-list";
                    } else {
                        return "redirect:user-list";
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
        if (role.equals("teacher") || role.equals("admin")) {
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
                        if (roleDelete.equals("admin")) {
                            roleDelete = "course_administrator";
                        }
                        // Performs deletion if it passes all checks
                        accountClientService.deleteRole(roleDelete, userId);

                        return "redirect:user-list";
                    }
                }
            }
        }
        return "redirect:user-list";
    }
}
