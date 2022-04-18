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
    Integer sortMode = 0;
    Integer ascDesc = 0;
    Boolean isSorted = false;
    List<User> users = new ArrayList<User>();

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

        PaginatedUsersResponse response = accountClientService.getPaginatedUsers(step, start, "name");
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
            @RequestParam(value="sortColumn") Integer sortColumn,
            Model model
    ) throws Exception {

        if (sortColumn == sortMode) {
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

    private void OrderList (PaginatedUsersResponse response) {
        for (UserResponse user : response.getUsersList()) {
            isSorted = false;
            if (users.size() == 0) {
                users.add(new User(user));
            }
            else {
                for (int i = 0; i < users.size(); i++) {
                    if (sortMode == 0) {
                        if (ascDesc == 0) {
                            if ((users.get(i).getFirstName().compareTo(user.getFirstName()) > 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        } else {
                            if ((users.get(i).getFirstName().compareTo(user.getFirstName()) <= 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        }
                    }
                    if (sortMode == 1) {
                        if (ascDesc == 0) {
                            if ((users.get(i).getLastName().compareTo(user.getLastName()) > 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        } else {
                            if ((users.get(i).getLastName().compareTo(user.getLastName()) <= 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        }
                    }
                    if (sortMode == 2) {
                        if (ascDesc == 0) {
                            if ((users.get(i).getUsername().compareTo(user.getUsername()) > 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        } else {
                            if ((users.get(i).getUsername().compareTo(user.getUsername()) <= 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        }
                    }
                    if (sortMode == 3) {
                        if (ascDesc == 0) {
                            if ((users.get(i).getNickname().compareTo(user.getNickname()) > 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        } else {
                            if ((users.get(i).getNickname().compareTo(user.getNickname()) <= 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        }
                    }
                    if (sortMode == 4) {
                        User newUser = new User(user);
                        if (ascDesc == 0) {
                            if ((users.get(i).roles().compareTo(newUser.roles()) > 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        } else {
                            if ((users.get(i).roles().compareTo(newUser.roles()) <= 0)) {
                                users.add(i, new User(user));
                                i = users.size();
                                isSorted = true;
                            }
                        }
                    }
                }
                if (!isSorted) {
                    users.add(new User(user));
                }
            }
        }
    }
}
