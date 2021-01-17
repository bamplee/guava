package im.prize.api.application;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TradeSummarySpecs {
    public enum SearchKey {
        REGION_CODE("regionCode"),
        BUILDING_CODE("buildingCode"),
        AREA_CODE("areaCode"),
        DATE("date");
//        DATE_BETWEEN("date");
//        LIKESGREATERTHAN("likes");

        private final String value;

        SearchKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static SearchKey convert(String value) {
            for (SearchKey b : SearchKey.values()) {
                if (b.getValue().equalsIgnoreCase(value)) {
                    return b;
                }
            }
            return null;
        }
    }

    public static Specification<TradeSummary> withTitle(String title) {
        return (Specification<TradeSummary>) ((root, query, builder) ->
            builder.equal(root.get("title"), title)
        );
    }

    public static Specification<TradeSummary> searchWith(Map<SearchKey, Object> searchKeyword) {
        return (Specification<TradeSummary>) ((root, query, builder) -> {
            List<Predicate> predicate = getPredicateWithKeyword(searchKeyword, root, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        });
    }

    private static List<Predicate> getPredicateWithKeyword(Map<SearchKey, Object> searchKeyword,
                                                           Root<TradeSummary> root,
                                                           CriteriaBuilder builder) {
        List<Predicate> predicate = new ArrayList<>();
        for (SearchKey key : searchKeyword.keySet()) {
            switch (key) {
                case BUILDING_CODE:
                case AREA_CODE:
//                case DATE:
                    predicate.add(builder.equal(
                        root.get(key.value), searchKeyword.get(key)
                    ));
                    break;
//                case LIKESGREATERTHAN:
//                    predicate.add(builder.greaterThan(
//                        root.get(key.value), Integer.valueOf(searchKeyword.get(key).toString())
//                    ));
//                    break;
                case REGION_CODE:
                    predicate.add(builder.like(
                        root.get(key.value), searchKeyword.get(key) + "%"
                    ));
                    break;
                case DATE:
                    predicate.add(builder.between(
                        root.get(key.value), searchKeyword.get(key) + "01", searchKeyword.get(key) + "31"
                    ));
                    break;
            }
        }
        return predicate;
    }
}
