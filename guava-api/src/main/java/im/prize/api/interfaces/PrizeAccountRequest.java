package im.prize.api.interfaces;

import lombok.Data;

@Data
public class PrizeAccountRequest {
    private Long userId;
    private Long sellerId;
    private Long accountId;
    private String loginId;
    private String loginPassword;
}
