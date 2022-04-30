package nz.ac.canterbury.seng302.portfolio.controller;

import java.io.IOException;
import java.security.Principal;

import com.google.protobuf.Timestamp;
import com.google.rpc.context.AttributeContext;
import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationFilter;
import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationToken;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthStateOrBuilder;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    private AuthState validAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("validtesttoken")
            .build();

    private AuthState invalidAuthState = AuthState.newBuilder()
            .setIsAuthenticated(true)
            .setNameClaimType("name")
            .setRoleClaimType("role")
            .setAuthenticationType("AuthenticationTypes.Federation")
            .setName("invalidtesttoken")
            .build();

    private UserResponse testUser = UserResponse.newBuilder()
            .setBio("testbio")
            .setCreated(Timestamp.newBuilder().setSeconds(10))
            .setEmail("test@email")
            .setFirstName("testfirstname")
            .setLastName("testlastname")
            .setMiddleName("testmiddlename")
            .setNickname("testnickname")
            .setPersonalPronouns("test/test")
            .build();

    private HandlerMethodArgumentResolver putAuthenticationPrincipal = new HandlerMethodArgumentResolver() {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().isAssignableFrom(AuthState.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return validAuthState;
        }
    };

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountClientService accountClientService;

    @MockBean
    AuthenticateClientService authenticateClientService;

    @Mock
    private AuthStateInformer authStateInformer = Mockito.mock(AuthStateInformer.class);

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(AccountController.class)
                .setCustomArgumentResolvers(putAuthenticationPrincipal)
                .build();
    }

    @Test
    public void getAccountWithValidCredentials() throws Exception {
        when(authStateInformer.getId(validAuthState)).thenReturn(1);
        when(accountClientService.getUserById(1)).thenReturn(testUser);
        mockMvc.perform(get("/account"))
        .andExpect(status().isOk());
    }


    @Test
    public void testTest() {
        assertEquals(true, true);
    }

}
