package im.prize.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrizeAccountDto {
    private Long accountId;
    private Long userId;
    private String sellerName;
    private String sellerLinkUrl;
    private String sellerLogoUrl;
    private String loginId;
}
