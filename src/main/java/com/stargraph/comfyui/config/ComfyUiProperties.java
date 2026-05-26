package com.stargraph.comfyui.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "comfyui")
public class ComfyUiProperties {

    private String baseUrl = "http://localhost:8000";
}
