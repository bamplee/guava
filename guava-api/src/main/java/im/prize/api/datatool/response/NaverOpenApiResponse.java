package im.prize.api.datatool.response;

import lombok.Data;

@Data
public class NaverOpenApiResponse {
    private String resultcode;
    private String message;
    private Response response;

    @Data
    public static class Response {
        private String id;
        private String email;
        private String name;
        private String profile_image;
        private String age;
        private String birthday;
        private String gender;
        private String nickname;
    }
}
