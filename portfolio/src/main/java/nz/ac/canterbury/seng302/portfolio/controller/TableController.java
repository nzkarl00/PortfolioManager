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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

@Controller
public class TableController {

    @Autowired
    private AccountClientService accountClientService;

    Integer sortMode = 0;
    Integer ascDesc = 0;
    Boolean isSorted = false;
    List<User> users = new ArrayList<User>();

    /**
     * control the displaying of account details
     * @param principal the auth token
     * @param model
     * @return string of where to go next
     */
    @GetMapping("/user-list")
    public String account(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {

        users.clear();
        isSorted = false;
        Integer id = AuthStateInformer.getId(principal);

        UserResponse userReply;
        userReply = accountClientService.getUserById(id); // Get the user

        model.addAttribute("date", DateParser.displayDate(userReply));

        PaginatedUsersResponse response = accountClientService.getPaginatedUsers(50, 0, "name");

        OrderList(response);
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
