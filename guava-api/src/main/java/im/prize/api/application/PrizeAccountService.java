package im.prize.api.application;

import im.prize.api.application.dto.PrizeAccountDto;

import java.util.List;

public interface PrizeAccountService {

    PrizeAccountDto createAccount(Long userId, Long sellerId, String loginId, String loginPassword);

//    PrizeAccountDto readAccount(Long userId, Long accountId);

    List<PrizeAccountDto> readAccountList(Long userId);

//    List<PrizeAccountDto> readAccountList(Long sellerId, Long userId);

//    PrizeAccountDto updateAccount();

    void deleteAccount(Long userId, Long accountId);
}
