package im.prize.api.infrastructure.persistence.jpa.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuavaBuildingSpecs {
    public enum SearchKey {
        ADDRESS("address"),
        NAME("name");

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

    public static Specification<GuavaBuilding> searchWith(Map<GuavaBuildingSpecs.SearchKey, Object> searchKeyword) {
        return (Specification<GuavaBuilding>) ((root, query, builder) -> {
            List<Predicate> predicate = getPredicateWithKeyword(searchKeyword, root, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        });
    }

    private static List<Predicate> getPredicateWithKeyword(Map<SearchKey, Object> searchKeyword,
                                                           Root<GuavaBuilding> root,
                                                           CriteriaBuilder builder) {
        List<Predicate> predicate = new ArrayList<>();
        for (SearchKey key : searchKeyword.keySet()) {
            switch (key) {
                case ADDRESS:
                case NAME:
                    predicate.add(builder.like(
                        root.get(key.value), "%" + StringUtils.join((searchKeyword.get(key).toString()).split(""), "%") + "%"
                    ));
                    break;
            }
        }
        return predicate;
    }
}
