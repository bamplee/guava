package im.prize.api.interfaces;

import im.prize.api.application.GuavaMatchService;
import im.prize.api.infrastructure.persistence.jpa.repository.GuavaMatchTemp;
import im.prize.api.interfaces.response.GuavaMatchResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/guava/match")
public class GuavaMatchController {
    private final GuavaMatchService guavaMatchService;

    public GuavaMatchController(GuavaMatchService guavaMatchService) {this.guavaMatchService = guavaMatchService;}

    @GetMapping
    GuavaMatchResponse match() {
        return guavaMatchService.match();
    }

    @GetMapping("/sync")
    void sync() {
        guavaMatchService.sync();
    }

    @GetMapping("/check")
    GuavaMatchTemp check(@RequestParam("tradeId") String tradeId,
                         @RequestParam("buildingId") String buildingId) {
        return guavaMatchService.check(tradeId, buildingId);
    }
}
