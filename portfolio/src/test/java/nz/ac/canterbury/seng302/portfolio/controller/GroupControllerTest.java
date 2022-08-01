package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.GroupRepoRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedGroupsResponse;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static nz.ac.canterbury.seng302.portfolio.common.CommonControllerUsage.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

    PaginatedGroupsResponse response = PaginatedGroupsResponse.newBuilder().build();
    GroupDetailsResponse groupDetailsResponse = GroupDetailsResponse.newBuilder().setGroupId(0).setLongName("Owl group").setShortName("OG").build();
    DeleteGroupResponse deleteGroupResponseSuccess = DeleteGroupResponse.newBuilder().setIsSuccess(true).setMessage("group has been deleted").build();


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    GroupsClientService groupsService;

    @MockBean
    GroupRepoRepository groupRepoRepository;

    @MockBean
    NavController navController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(GroupController.class)
                .build();
    }

    static MockedStatic<AuthStateInformer> utilities;

    @BeforeAll
    public static void open() {
        utilities = Mockito.mockStatic(AuthStateInformer.class);
    }

    @AfterAll
    public static void close() {
        utilities.close();
    }

    @Test
    void getGroupsAsAdmin() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(groupsService.getGroups(10, 0, true)).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/groups"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(view().name("groups"));
    }

    @Test
    void getGroupsAsAdmin_pageTooBig() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(groupsService.getGroups(10, 1, true)).thenReturn(response);
        when(groupsService.getGroups(10, 0, true)).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/groups?page=1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(view().name("groups"));
    }

    @Test
    void getGroupsAsAdmin_pageTooSmall() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(groupsService.getGroups(10, 0, true)).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/groups?page=-1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(view().name("groups"));
    }

    @Test
    void getGroupsAsAdmin_pageTooSmallExtreme() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");
        when(groupsService.getGroups(10, 0, true)).thenReturn(response);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);
        mockMvc.perform(get("/groups?page=-100"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(view().name("groups"));
    }

    @Test
    public void postGroupsAsTeacher_DeleteGroup() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateTeacher, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateTeacher)).thenReturn("teacher");

        when(groupsService.getGroup(0)).thenReturn(groupDetailsResponse);
        when(groupsService.delete(0)).thenReturn(deleteGroupResponseSuccess);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        mockMvc.perform(post("/delete-group").param("groupId", String.valueOf(0)))
                .andExpect(status().is3xxRedirection()) // given this should move to details once deleted redirection is expected
                .andExpect(view().name("redirect:groups"));

        verify(groupsService, atLeastOnce()).getGroup(groupDetailsResponse.getGroupId());
        verify(groupsService, atLeastOnce()).delete(0);
    }

    @Test
    public void postGroupsAsAdmin_DeleteGroup() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateAdmin, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateAdmin)).thenReturn("admin");

        when(groupsService.getGroup(0)).thenReturn(groupDetailsResponse);
        when(groupsService.delete(0)).thenReturn(deleteGroupResponseSuccess);

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        mockMvc.perform(post("/delete-group").param("groupId", String.valueOf(0)))
                .andExpect(status().is3xxRedirection()) // given this should move to details once deleted redirection is expected
                .andExpect(view().name("redirect:groups"));

        verify(groupsService, atLeastOnce()).getGroup(groupDetailsResponse.getGroupId());
        verify(groupsService, atLeastOnce()).delete(0);
    }

    @Test
    public void postGroupsAsStudent_DeleteGroup() throws Exception {
        //Create a mocked security context to return the AuthState object we made above (aka. validAuthState)
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        when(mockedSecurityContext.getAuthentication())
                .thenReturn(new PreAuthenticatedAuthenticationToken(validAuthStateStudent, ""));

        utilities.when(() -> AuthStateInformer.getRole(validAuthStateStudent)).thenReturn("student");

        // Configuring Spring to use the mocked SecurityContext
        SecurityContextHolder.setContext(mockedSecurityContext);

        mockMvc.perform(post("/delete-group").param("groupId", String.valueOf(1)))
                .andExpect(status().is3xxRedirection()) // given this should move to details once deleted redirection is expected
                .andExpect(view().name("redirect:groups"));

        verify(groupsService, never()).getGroup(groupDetailsResponse.getGroupId());
        verify(groupsService, never()).delete(groupDetailsResponse.getGroupId());
    }
}
