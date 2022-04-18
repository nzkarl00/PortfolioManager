package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.web.bind.annotation.*;

public class FragmentsController {

    @GetMapping("/fragments")
    public String getHome() {
        return "fragments.html";
    }

}
