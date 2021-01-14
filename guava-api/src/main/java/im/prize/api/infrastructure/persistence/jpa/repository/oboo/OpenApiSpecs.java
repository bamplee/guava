package im.prize.api.infrastructure.persistence.jpa.repository.oboo;

import im.prize.api.domain.oboo.OpenApiTradeInfo;
import org.springframework.data.jpa.domain.Specification;

public class OpenApiSpecs {
    public enum SearchKey {
        BUILDING_ID("buildingId"),
        DATE("date"),
        LIKESGREATERTHAN("likes");

        private final String value;

        SearchKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Specification<OpenApiTradeInfo> withArea(String area) {
            return (Specification<OpenApiTradeInfo>) ((root, query, builder) ->
                builder.equal(root.get("area"), area)
            );
        }
    }
}
