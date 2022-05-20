package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.EventUpdate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

@Controller
public class EventUpdateController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public EventUpdate greeting(EventUpdate message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new EventUpdate("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @GetMapping("/hello")
    public String getHtml() {
        return "socketTest";
    }
}
