package im.prize.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PdfResponse {
    private String dataFileName;
    private Integer dataPage;
    private Integer dataTable;
    private List<List<String>> row;

    @lombok.Data
    public static class Data {
        private List<String> td;
    }
}
