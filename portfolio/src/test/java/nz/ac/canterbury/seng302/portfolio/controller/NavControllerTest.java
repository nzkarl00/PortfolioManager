package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import com.google.protobuf.TimestampOrBuilder;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.DateParser;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
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
    public void updateModelForNavTest() {
        Model model = mock(Model.class);
        AuthState principal = mock(AuthState.class);

        UserResponse userResponse = UserResponse.newBuilder().setUsername("timmy").build();

        utilities.when(() -> DateParser.displayDate(userResponse)).thenReturn(" 28 April 2022 (1 Month)"); // not actually correct but hey

        NavController navController = new NavController();
        navController.updateModelForNav(principal, model, userResponse, 1);

        verify(model).addAttribute("photo", "null/image/1"); // @value hasn't been mocked so it just imports null
        // mocking said value would more be an exercise in mocking than testing the value auto-wiring
        verify(model).addAttribute("username", "timmy");
        verify(model).addAttribute("date", " 28 April 2022 (1 Month)");
    }

}