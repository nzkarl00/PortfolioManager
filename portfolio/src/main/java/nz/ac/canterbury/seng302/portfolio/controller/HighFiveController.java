package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Responsible for high five network requests
 */
@Controller
public class HighFiveController {

    @Autowired
    HighFiveRepository highFiveRepository;

    /**
     * Adds a high five to a piece of evidence
     * @param evidenceId Evidence to add the high five to
     * @param principal Authenticated user that posted the high five
     * @return
     */
    @PostMapping("/high-five")
    public String deleteEvidence(@RequestParam(value = "evidenceId") String evidenceId,
                                 @AuthenticationPrincipal AuthState principal) {
        if (highFiveRepository.findById == null) {
            AuthStateInformer.getId(principal);
        }
    }
}
