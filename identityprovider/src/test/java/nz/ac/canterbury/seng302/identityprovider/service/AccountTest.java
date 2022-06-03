package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AccountTest {

    @Mock
    static AccountProfileRepository repository;

    @InjectMocks
    private static Account account = new Account();

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    AccountProfile testAccount = new AccountProfile("String username", "String passwordHash",
        new Date(), "String bio", "String email", "DEFAULT",
        "String firstName", "String lastName", "String pronouns");

    @Test
    public void getAllAccounts_blueSky() {
        List<AccountProfile> expected = new ArrayList<>(List.of(testAccount));
        when(repository.findAll()).thenReturn(expected);
        assertEquals(expected, account.getAllAccounts());
    }

    @Test
    public void getAccountById_blueSky() throws Exception {
        when(repository.findById(1)).thenReturn(testAccount);
        assertEquals(testAccount, account.getAccountById(1));
    }

    @Test
    public void getAccountById_fails() {
        assertThrows(Exception.class, () -> {
            account.getAccountById(1);
        });
    }

    @Test
    public void getAccountByEmail_blueSky() throws Exception {
        when(repository.findByEmail("email@email")).thenReturn(testAccount);
        assertEquals(testAccount, account.getAccountByEmail("email@email"));
    }

    @Test
    public void getAccountByEmail_fails() {
        assertThrows(Exception.class, () -> {
            account.getAccountByEmail("email@email");
        });
    }

    @Test
    public void getAccountByUsername_blueSky() throws Exception {
        when(repository.findByUsername("username")).thenReturn(testAccount);
        assertEquals(testAccount, account.getAccountByUsername("username"));
    }

    @Test
    public void getAccountByUsername_fails() {
        assertThrows(Exception.class, () -> {
            account.getAccountByUsername("username");
        });
    }
}