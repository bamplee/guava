package net.moboo.batch.hgnn.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

//@FeignClient(name = "naver-land-client", url = "https://m.land.naver.com/complex/")
@FeignClient(name = "naver-land-client", url = "https://new.land.naver.com/api/articles/")
public interface NaverLandClient {

    /*
    rletTypeCd: A01=아파트, A02=오피스텔, B01=분양권, 주택=C03, 토지=E03, 원룸=C01, 상가=D02, 사무실=D01, 공장=E02, 재개발=F01, 건물=D03
    tradeTypeCd (거래종류): all=전체, A1=매매, B1=전세, B2=월세, B3=단기임대
    hscpTypeCd (매물종류): 아파트=A01, 주상복합=A03, 재건축=A04 (복수 선택 가능)
    cortarNo(법정동코드): (예: 1168010600 서울시, 강남구, 대치동)
    */
    @GetMapping("/getComplexArticleList")
    NaverLandResponse getComplexArticleList(@RequestParam("hscpNo") String hscpNo,
                                            @RequestParam(value = "cortarNo", required = false) String cortarNo,
                                            @RequestParam(value = "tradTpCd", defaultValue = "A1", required = false) String tradTpCd,
                                            @RequestParam(value = "order", defaultValue = "point_", required = false) String order,
                                            @RequestParam(value = "showR0", defaultValue = "N", required = false) String showR0,
                                            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page);

    @GetMapping("/complex/{hscpNo}")
    NaverPcLandResponse getPcData(@PathVariable("hscpNo") String hscpNo,
                                  @RequestParam("tradeType") String tradeType,
                                  @RequestParam("page") Integer page,
                                  @RequestParam(value = "sameAddressGroup", defaultValue = "true") String sameAddressGroup);
}
