package com.stargraph.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MinIO 对象存储配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /** MinIO API 地址 */
    private String endpoint;

    /** 访问账号 */
    private String accessKey;

    /** 访问密码 */
    private String secretKey;

    /** 公共桶名称 */
    private String bucket;

    /** 公共访问 URL 前缀 */
    private String publicBaseUrl;
}
