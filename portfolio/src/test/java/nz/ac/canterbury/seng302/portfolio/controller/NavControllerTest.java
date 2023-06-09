package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NavControllerTest {

    // setting up and closing the mocked static authStateInformer
    static MockedStatic<DateParser> utilities;

    @BeforeAll
    public static void open() {
        utilities = Mockito.mockStatic(DateParser.class);
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }

    @Test
    void updateModelForNavTest() {
        Model model = mock(Model.class);
        AuthState principal = mock(AuthState.class);

        UserResponse userResponse = UserResponse.newBuilder().setUsername("timmy").build();

        utilities.when(() -> DateParser.displayDate(any(UserResponse.class), any(Date.class))).thenReturn(" 28 April 2022 (1 Month)"); // not actually correct but hey

        NavController navController = new NavController();
        navController.updateModelForNav(principal, model, userResponse, 1);

        verify(model).addAttribute("photo", "null/image/1"); // @value hasn't been mocked so it just imports null
        // mocking said value would more be an exercise in mocking than testing the value auto-wiring
        verify(model).addAttribute("username", "timmy");
        verify(model).addAttribute("dateSince", " 28 April 2022 (1 Month)");
    }

}