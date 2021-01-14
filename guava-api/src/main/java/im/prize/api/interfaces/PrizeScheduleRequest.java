package im.prize.api.interfaces;

import lombok.Data;

@Data
public class PrizeScheduleRequest {
    private Long scheduleId;
    private Long userId;
    private Long accountId;
    private Long dealId;
    private String size;
}
