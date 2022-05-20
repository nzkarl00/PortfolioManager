package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.EventUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

@Controller
public class EventUpdateController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public EventUpdate greeting(EventUpdate message) throws Exception {
        Thread.sleep(1000); // simulated delay
        sendAnother();
        return new EventUpdate("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    public void sendAnother() {
        System.out.println("yeet");
        this.template.convertAndSend("/topic/greetings", new EventUpdate("HAVE ANOTHER!!!"));
    }

    @GetMapping("/hello")
    public String getHtml() {
        return "socketTest";
    }
}
