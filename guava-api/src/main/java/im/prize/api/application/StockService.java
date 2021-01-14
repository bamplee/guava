package im.prize.api.application;

import im.prize.api.application.dto.CompanyActivityDto;
import im.prize.api.application.dto.CompanyDto;
import im.prize.api.application.dto.DailyActivityDto;
import im.prize.api.application.dto.PriceInfoDto;

import java.util.List;

public interface StockService {
    List<CompanyDto> getCompanyList(String query);

    List<DailyActivityDto> getActivityList(String query, Integer page, Integer size);

    PriceInfoDto getCurrentPriceInfo(String companyId);

    List<PriceInfoDto> getPrices(String companyId, String since);

    List<CompanyActivityDto> getCompanyActivities(String companyId, String since);
}
