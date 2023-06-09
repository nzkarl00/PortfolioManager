package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.HighFive;
import nz.ac.canterbury.seng302.portfolio.model.evidence.HighFiveRepository;
import nz.ac.canterbury.seng302.portfolio.model.userGroups.User;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

/**
 * Responsible for high five network requests
 */
@Controller
public class HighFiveController {

    @Autowired
    HighFiveRepository highFiveRepository;
    @Autowired
    EvidenceRepository evidenceRepository;
    @Autowired
    AccountClientService accountClientService;

    Logger logger = LoggerFactory.getLogger(HighFiveController.class);

    /**
     * Adds a high five to a piece of evidence
     * @param evidenceId Evidence to add the high five to
     * @param principal Authenticated user that posted the high five
     * @return
     */
    @PostMapping("/high-five")
    @ResponseBody
    @Transactional
    public String deleteEvidence(@RequestParam(value = "evidenceId") String evidenceId,
                                 @AuthenticationPrincipal AuthState principal) {
        int userId = AuthStateInformer.getId(principal);
        User user = new User(accountClientService.getUserById(userId));
        Evidence parentEvidence = evidenceRepository.findById(Integer.parseInt(evidenceId));
        if (parentEvidence != null) {
            HighFive highFive = highFiveRepository.findByParentEvidenceAndParentUserId(parentEvidence, userId);
            if (highFive == null) {
                logger.info("[HighFiveController] adding new HighFive to evidence: " + parentEvidence.getParentUserId());
                highFiveRepository.save(new HighFive(parentEvidence, userId, user.getFirstName(), user.getLastName()));
                return "added";
            } else {
                logger.info("[HighFiveController] deleting HighFive: " + highFive.getId());

                parentEvidence.removeHighFive(highFive);
                evidenceRepository.save(parentEvidence);
                highFiveRepository.delete(highFive);
                return "deleted";
            }
        }
        return "error";
    }
}
