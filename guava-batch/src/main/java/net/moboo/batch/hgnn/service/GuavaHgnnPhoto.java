package net.moboo.batch.hgnn.service;

import lombok.Data;

@Data
public class GuavaHgnnPhoto {
    private String originalname;
    private String mimetype;
    private Integer size;
    private String path;
    private String bucket;
    private String key;
}
