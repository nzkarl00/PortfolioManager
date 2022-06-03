package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.model.AccountProfile;
import nz.ac.canterbury.seng302.identityprovider.model.AccountProfileRepository;
import nz.ac.canterbury.seng302.identityprovider.model.RolesRepository;
import nz.ac.canterbury.seng302.identityprovider.service.Account;
import nz.ac.canterbury.seng302.identityprovider.util.FileSystemUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProfilePhotoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfilePhotoControllerTest {

    @MockBean
    Account accountService;

    @MockBean
    AccountProfileRepository repo;

    @MockBean
    RolesRepository roleRepo;

    @MockBean
    FileSystemUtils fsUtils;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(ProfilePhotoController.class)
            .build();
    }

    AccountProfile testAccount = new AccountProfile("String username", "String passwordHash",
        new Date(), "String bio", "String email", "DEFAULT",
        "String firstName", "String lastName", "String pronouns");

    @Test
    public void getPhotoTest() throws Exception {

        when(repo.findById(1)).thenReturn(testAccount);

        //https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
        MvcResult result = mockMvc.perform(get("/image/1"))
            .andExpect(status().isOk())
            .andReturn(); // Whether to return the status "200 OK"

        String contentType = result.getResponse().getContentType();
        assertEquals("image/jpeg", contentType); // assert it is giving us a jpeg
    }
}