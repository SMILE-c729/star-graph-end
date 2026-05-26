package com.stargraph.comfyui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatsResponse {

    private Map<String, Object> system;
    private List<Map<String, Object>> devices;
}
