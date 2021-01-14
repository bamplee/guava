package net.moboo.batch.hgnn.feign;

import lombok.Data;

@Data
public class NaverLambdaResponse {
    private String statusCode;
    private NaverLandResponse body;
}
