package im.prize.api.interfaces;

import im.prize.api.application.PrizeAccountService;
import im.prize.api.application.dto.PrizeAccountDto;
import im.prize.api.datatool.PrizeLambdaClient;
import im.prize.api.datatool.request.NikeDealLoginRequest;
import im.prize.api.datatool.response.NikeDealLoginResponse;
import im.prize.api.domain.entity.PrizeUser;
import im.prize.api.interfaces.request.PrizeAccountRequest;
import im.prize.api.interfaces.request.PrizeScheduleRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PrizeApiController {

    private final PrizeAccountService prizeAccountService;

    public PrizeApiController(PrizeAccountService prizeAccountService) {
        this.prizeAccountService = prizeAccountService;
    }

    @PostMapping("/accounts")
    public PrizeAccountDto createAccount(@AuthenticationPrincipal(expression = "user") PrizeUser user,
                                         @RequestBody PrizeAccountRequest prizeAccountRequest) {
        return prizeAccountService.createAccount(user.getId(),
                                                 prizeAccountRequest.getSellerId(),
                                                 prizeAccountRequest.getLoginId(),
                                                 prizeAccountRequest.getLoginPassword());
    }

    @DeleteMapping("/accounts")
    public void deleteAccount(@AuthenticationPrincipal(expression = "user") PrizeUser user,
                              @RequestBody PrizeAccountRequest prizeAccountRequest) {
        prizeAccountService.deleteAccount(user.getId(),
                                          prizeAccountRequest.getAccountId());
    }

    @GetMapping("/accounts")
    public List<PrizeAccountDto> readAccount(@AuthenticationPrincipal(expression = "user") PrizeUser user) {
        return prizeAccountService.readAccountList(user.getId());
    }

//    @GetMapping("/accounts/{accountId}")
//    public PrizeAccountDto readAccount(@PathVariable("accountId") Long accountId) {
//        // fixme
//        Long userId = 1l;
//        return prizeAccountService.readAccount(userId, accountId);
//    }

}
