package com.stargraph.comfyui.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptRequest {

    @SerializedName("client_id")
    private String clientId;

    private Map<String, Object> prompt;
}
