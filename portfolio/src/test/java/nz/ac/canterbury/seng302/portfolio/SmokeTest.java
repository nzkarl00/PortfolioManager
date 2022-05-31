package nz.ac.canterbury.seng302.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import nz.ac.canterbury.seng302.portfolio.controller.AccountController;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@SpringBootTest
@ContextConfiguration(loader= AnnotationConfigContextLoader.class, classes={SecurityConfig.class, AccountConfig.class})
public class SmokeTest {

    @Autowired
    private AccountController controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}