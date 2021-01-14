package im.prize.api.application;

import im.prize.api.infrastructure.persistence.jpa.repository.GuavaBuildingArea;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GuavaUtils {
    public static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();
        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                                       .map(ke -> ke.apply(t))
                                       .collect(Collectors.toList());
            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

    public static String getTradePrice(String price) {
        if (price != null) {
            if (price.contains("억")) {
                price = price.replace(",", "").replace(" ", "");
                String[] splitPrice = price.split("억");
                splitPrice[0] += "0000";
                for (String s : splitPrice) {
                    price = String.valueOf(NumberUtils.toInt(price) + NumberUtils.toInt(s));
                }
            } else if (price.contains("천")) {
                price = String.valueOf(NumberUtils.toDouble(price.replace("천", "")) * 1000);
            }
            int intPrice = NumberUtils.toInt(price);
            double result = 0;
            String priceVal = "";
            if (intPrice >= 10000) {
                result = intPrice / 10000.0;
                priceVal = "억";
            } else if (intPrice >= 1000) {
                result = intPrice / 1000.0;
                priceVal = "천";
            }
            result = Math.round(result * 100) / 100.0;
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(result) + priceVal;
        }
        return "0";
    }

    public static String getSummaryPrice(String price) {
        if (price != null) {
            price = price.replace(",", "");
            int intPrice = NumberUtils.toInt(price);
            double result = 0;
            String priceVal = "";
            if (intPrice >= 10000) {
                result = intPrice / 10000.0;
                priceVal = "억";
            } else if (intPrice >= 1000) {
                result = intPrice / 1000.0;
                priceVal = "천";
            }
            result = Math.round(result * 10) / 10.0;
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(result) + priceVal;
        }
        return "0";
    }

    public static RegionType convertLevelToRegionType(Integer level) {
        if (level > 9) {
            return RegionType.SIDO;
        }
        if (level > 7) {
            return RegionType.SIGUNGU;
        }
        if (level > 5) {
            return RegionType.DONG;
        }
        return RegionType.BUILDING;
    }

    public static GuavaBuildingArea getAreaByPrivateArea(List<GuavaBuildingArea> guavaBuildingAreaList, String privateArea) {
        Optional<GuavaBuildingArea> first = Optional.empty();
        first = guavaBuildingAreaList.stream()
                                     .filter(x -> x.getPrivateArea() == Double.parseDouble(String.format(
                                         "%.2f",
                                         Double.parseDouble(privateArea))))
                                     .findFirst();
        if (!first.isPresent()) {
            double min = Double.MAX_VALUE;
            for (GuavaBuildingArea guavaBuildingArea : guavaBuildingAreaList) {
                double a = Math.abs(guavaBuildingArea.getPrivateArea() - Double.valueOf(privateArea));  // 절대값을 취한다.
                if (min > a) {
                    min = a;
                    first = Optional.of(guavaBuildingArea);
                }
            }
        }

        return first.orElse(GuavaBuildingArea.builder().privateArea(0d).publicArea(0d).build());
    }
}
