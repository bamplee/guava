package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.TradeArticle;
import im.prize.api.domain.oboo.TradeItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TradeArticleRepository extends JpaRepository<TradeArticle, Long> {
    List<TradeArticle> findByPortalId(String portalId, Pageable pageable);

    @Query(value = "select * from trade_article where region_code = ?1 and area2 between ?2 and ?3 order by article_confirm_ymd desc",
           nativeQuery = true)
    List<TradeArticle> findByRegionCodeAndArea2Between(String regionCode, Integer startArea, Integer endArea, Pageable pageable);

    @Query(value = "select * from trade_article where region_code like ?1 and area2 between ?2 and ?3 order by article_confirm_ymd desc",
           nativeQuery = true)
    List<TradeArticle> findByRegionCodeLikeAndArea2Between(String regionCode, Integer startArea, Integer endArea, Pageable pageable);

    List<TradeArticle> findByBuildingCodeAndArea2(String buildingCode, String area2);

    List<TradeArticle> findByBuildingCodeAndArea1AndArea2(String buildingCode, String publicArea, String privateArea);
}
