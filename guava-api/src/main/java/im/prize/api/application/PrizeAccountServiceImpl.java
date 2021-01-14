package im.prize.api.application;

import im.prize.api.application.dto.PrizeAccountDto;
import im.prize.api.domain.entity.PrizeAccount;
import im.prize.api.domain.entity.PrizeUser;
import im.prize.api.infrastructure.persistence.jpa.repository.PrizeAccountRepository;
import im.prize.api.infrastructure.persistence.jpa.repository.PrizeUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrizeAccountServiceImpl implements PrizeAccountService {
    private final PrizeAccountRepository prizeAccountRepository;
    private final PrizeUserRepository prizeUserRepository;

    public PrizeAccountServiceImpl(PrizeAccountRepository prizeAccountRepository,
                                   PrizeUserRepository prizeUserRepository) {
        this.prizeAccountRepository = prizeAccountRepository;
        this.prizeUserRepository = prizeUserRepository;
    }

    @Override
    public PrizeAccountDto createAccount(Long userId, Long sellerId, String loginId, String loginPassword) {
        Optional<PrizeUser> optionalPrizeUser = prizeUserRepository.findById(userId);
        if (!optionalPrizeUser.isPresent()) {
            // fixme
        }
        PrizeAccount prizeAccount = PrizeAccount.builder()
                                                .user(optionalPrizeUser.get())
                                                .loginId(loginId)
                                                .loginPassword(loginPassword)
                                                .build();
        return this.transform(prizeAccountRepository.save(prizeAccount));
    }

//    @Override
//    public PrizeAccountDto readAccount(Long userId, Long accountId) {
//        Optional<PrizeUser> optionalPrizeUser = prizeUserRepository.findById(userId);
//        if (!optionalPrizeUser.isPresent()) {
//            // fixme
//        }
//        Optional<PrizeAccount> optionalPrizeAccount = prizeAccountRepository.findById(accountId);
//        if (!optionalPrizeAccount.isPresent()) {
//            // fixme
//        }
//        return this.transform(optionalPrizeAccount.get());
//    }

    @Override
    public List<PrizeAccountDto> readAccountList(Long userId) {
        Optional<PrizeUser> optionalPrizeUser = prizeUserRepository.findById(userId);
        if (!optionalPrizeUser.isPresent()) {
            // fixme
        }
        return prizeAccountRepository.findByUser(optionalPrizeUser.get())
                                     .stream()
                                     .map(this::transform)
                                     .collect(Collectors.toList());
    }

//    @Override
//    public List<PrizeAccountDto> readAccountList(Long sellerId, Long userId) {
//        Optional<PrizeSeller> optionalPrizeSeller = prizeSellerRepository.findById(sellerId);
//        if (!optionalPrizeSeller.isPresent()) {
//            // fixme
//        }
//        Optional<PrizeUser> optionalPrizeUser = prizeUserRepository.findById(userId);
//        if (!optionalPrizeUser.isPresent()) {
//            // fixme
//        }
//        return prizeAccountRepository.findBySellerAndUser(optionalPrizeSeller.get(), optionalPrizeUser.get())
//                                     .stream()
//                                     .map(this::transform)
//                                     .collect(Collectors.toList());
//    }

    @Override
    public void deleteAccount(Long userId, Long accountId) {
        prizeAccountRepository.deleteById(accountId);
    }

    private PrizeAccountDto transform(PrizeAccount prizeAccount) {
        return PrizeAccountDto.builder()
                              .accountId(prizeAccount.getId())
                              .userId(prizeAccount.getUser().getId())
                              .loginId(prizeAccount.getLoginId())
                              .build();
    }
}
