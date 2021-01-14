package net.moboo.batch.hgnn.feign;

import lombok.Data;

@Data
public class NaverLambdaRequest {
    private String hscpNo;
    private Integer page;
}
