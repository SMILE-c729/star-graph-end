package com.stargraph.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class SubmitTaskRequest {

    @NotBlank(message = "clientId 不能为空")
    private String clientId;

    @NotNull(message = "prompt 不能为空")
    private Map<String, Object> prompt;
}
