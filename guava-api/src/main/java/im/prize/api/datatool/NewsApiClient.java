package im.prize.api.datatool;

import im.prize.api.datatool.response.NewsApiResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "newsApiClient", url = "http://newsapi.org/v2/")
public interface NewsApiClient {
    @Cacheable(value = "getNewsList")
    @GetMapping("/everything")
    NewsApiResponse getNewsList(@RequestParam("q") String q,
                                @RequestParam("from") String from,
                                @RequestParam("sortBy") String sortBy,
                                @RequestParam("apiKey") String apiKey);
}
