package com.stargraph.comfyui.controller;

import com.stargraph.common.response.Result;
import com.stargraph.comfyui.dto.SubmitTaskRequest;
import com.stargraph.comfyui.client.handler.ComfyuiMessageHandler;
import com.stargraph.comfyui.model.PromptRequest;
import com.stargraph.comfyui.model.PromptResponse;
import com.stargraph.comfyui.model.QueueDeleteRequest;
import com.stargraph.comfyui.model.QueueResponse;
import com.stargraph.comfyui.model.SystemStatsResponse;
import com.stargraph.comfyui.properties.ComfyUiProperties;
import com.stargraph.comfyui.service.ComfyUiService;
import com.stargraph.comfyui.support.MultipartBodyFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ComfyUI REST 控制器。
 * 作为 ComfyUI 服务器的 HTTP 代理层，所有接口统一使用 /api/comfyui 前缀。
 * 将前端请求转发给 ComfyUiService，返回统一的 Result 格式响应。
 * 图片预览接口直接返回二进制流（ResponseEntity），不走 Result 封装。
 */
@RestController
@RequestMapping("/api/comfyui")
@RequiredArgsConstructor
public class ComfyUiController {

    private final ComfyUiService comfyUiService;
    private final ComfyUiProperties comfyUiProperties;
    private final ComfyuiMessageHandler comfyuiMessageHandler;
    private final MultipartBodyFactory multipartBodyFactory;

    /** 获取历史记录列表 */
    @GetMapping("/history")
    public Result<Map<String, Object>> getHistory(
            @RequestParam(required = false) String maxItems) throws IOException {
        return Result.ok(comfyUiService.getHistory(maxItems));
    }

    /** 根据 prompt_id 获取单条历史记录 */
    @GetMapping("/history/{promptId}")
    public Result<Map<String, Object>> getHistoryById(@PathVariable String promptId) throws IOException {
        return Result.ok(comfyUiService.getHistoryById(promptId));
    }

    /** 查看后端与 ComfyUI WebSocket 的连接状态 */
    @GetMapping("/ws/status")
    public Result<Map<String, Object>> getWebSocketStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("comfyUiClientId", comfyUiProperties.getClientId());
        status.put("comfyUiConnected", comfyuiMessageHandler.isConnected());
        status.put("sessionId", comfyuiMessageHandler.getSessionId());
        return Result.ok(status);
    }

    /**
     * 预览/下载生成的图片。
     * 不走 Result 封装，直接返回图片二进制流，Content-Type 从 ComfyUI 响应中获取。
     * 使用 try-with-resources 确保 ResponseBody 流被关闭。
     */
    @GetMapping("/view")
    public ResponseEntity<byte[]> previewImage(
            @RequestParam String filename,
            @RequestParam(required = false, defaultValue = "output") String type,
            @RequestParam(required = false, defaultValue = "") String subfolder) throws IOException {
        try (ResponseBody responseBody = comfyUiService.previewImage(filename, type, subfolder)) {
            byte[] imageBytes = responseBody.bytes();
            // 优先使用 ComfyUI 返回的 Content-Type，兜底用 image/png
            String contentType = responseBody.contentType() != null
                    ? responseBody.contentType().toString()
                    : "image/png";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(imageBytes);
        }
    }

    /** 获取 ComfyUI 服务器系统信息（OS、Python、GPU 等） */
    @GetMapping("/system-stats")
    public Result<SystemStatsResponse> getSystemStats() throws IOException {
        return Result.ok(comfyUiService.getSystemStats());
    }

    /** 获取指定节点的配置信息（输入/输出参数定义） */
    @GetMapping("/object-info/{nodeName}")
    public Result<Map<String, Object>> getObjectInfo(@PathVariable String nodeName) throws IOException {
        return Result.ok(comfyUiService.getObjectInfo(nodeName));
    }

    /** 取消当前正在执行的任务 */
    @PostMapping("/interrupt")
    public Result<Void> interrupt() throws IOException {
        comfyUiService.interrupt();
        return Result.ok();
    }

    /** 获取当前任务队列状态（正在执行 + 等待执行） */
    @GetMapping("/queue")
    public Result<QueueResponse> getQueue() throws IOException {
        return Result.ok(comfyUiService.getQueue());
    }

    /** 从等待队列中删除指定任务，接收 prompt_id 列表 */
    @PostMapping("/queue/delete")
    public Result<Void> deleteFromQueue(@RequestBody List<String> deleteIds) throws IOException {
        QueueDeleteRequest request = QueueDeleteRequest.builder()
                .delete(deleteIds)
                .build();
        comfyUiService.deleteFromQueue(request);
        return Result.ok();
    }

    /** 获取当前提示词配置信息 */
    @GetMapping("/prompt")
    public Result<Map<String, Object>> getPrompt() throws IOException {
        return Result.ok(comfyUiService.getPrompt());
    }

    /** 提交工作流任务到 ComfyUI，返回包含 prompt_id 和队列编号的响应 */
    @PostMapping("/prompt")
    public Result<PromptResponse> submitPrompt(
            @Valid @RequestBody SubmitTaskRequest request) throws IOException {
        PromptRequest promptRequest = PromptRequest.builder()
                .clientId(comfyUiProperties.getClientId())
                .prompt(request.getPrompt())
                .build();
        PromptResponse response = comfyUiService.submitPrompt(promptRequest);
        return Result.ok(response);
    }

    /** 上传图片到 ComfyUI */
    @PostMapping("/upload/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        MultipartBody.Part body = multipartBodyFactory.toImagePart(file);
        return Result.ok(comfyUiService.uploadImage(body));
    }

    /** 上传蒙版图片到 ComfyUI（用于 inpaint 等场景） */
    @PostMapping("/upload/mask")
    public Result<Map<String, Object>> uploadMask(
            @RequestParam("image") MultipartFile file,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "") String subfolder,
            @RequestParam(required = false) String originalRef) throws IOException {
        MultipartBody.Part imagePart = multipartBodyFactory.toImagePart(file);
        okhttp3.RequestBody typeBody = multipartBodyFactory.toTextBody(type);
        okhttp3.RequestBody subfolderBody = multipartBodyFactory.toTextBody(subfolder);
        okhttp3.RequestBody originalRefBody = multipartBodyFactory.toTextBody(originalRef);
        return Result.ok(comfyUiService.uploadMask(imagePart, typeBody, subfolderBody, originalRefBody));
    }
}
