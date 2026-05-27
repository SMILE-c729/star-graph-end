package com.stargraph.comfyui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * ComfyUI 系统信息响应体。
 * 对应 ComfyUI API 的 GET /system_stats 接口。
 * system 包含 OS 和 Python 版本等基础信息，devices 列出可用的计算设备（GPU）及其资源信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatsResponse {

    /** 系统基础信息（os、python_version 等） */
    private Map<String, Object> system;

    /** 计算设备列表，每个设备包含 name、type、vram_total、vram_free 等信息 */
    private List<Map<String, Object>> devices;
}
