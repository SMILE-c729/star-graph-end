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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@Tag(name = "ComfyUI接口", description = "ComfyUI 历史记录、任务提交、队列、图片预览、图片上传等代理接口。")
@SecurityRequirement(name = "JWT")
public class ComfyUiController {
    // ComfyUI 服务
    private final ComfyUiService comfyUiService;
    // ComfyUI 配置属性
    private final ComfyUiProperties comfyUiProperties;
    // ComfyUI 消息处理器
    private final ComfyuiMessageHandler comfyuiMessageHandler;
    // MultipartBody 工厂
    private final MultipartBodyFactory multipartBodyFactory;

    /** 获取历史记录列表 */
    @GetMapping("/history")
    @Operation(
            summary = "获取历史记录列表",
            description = "从 ComfyUI 查询历史任务记录。可通过 maxItems 限制返回条数，不传则使用 ComfyUI 默认值。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Map<String, Object>> getHistory(
            @Parameter(description = "最大返回条数；不传则返回 ComfyUI 默认数量。", example = "10")
            @RequestParam(required = false) String maxItems) throws IOException {
        return Result.ok(comfyUiService.getHistory(maxItems));
    }

    /** 根据 prompt_id 获取单条历史记录 */
    @GetMapping("/history/{promptId}")
    @Operation(
            summary = "获取单条历史记录",
            description = "根据任务提交返回的 prompt_id 查询该任务的执行结果、输出图片等历史详情。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Map<String, Object>> getHistoryById(
            @Parameter(description = "任务 prompt_id，来自提交任务接口返回值。", example = "2f7a4d51-8c0e-4c2d-a4ef-1b2c3d4e5f60")
            @PathVariable String promptId) throws IOException {
        return Result.ok(comfyUiService.getHistoryById(promptId));
    }

    /** 查看后端与 ComfyUI WebSocket 的连接状态 */
    @GetMapping("/ws/status")
    @Operation(
            summary = "查看 WebSocket 连接状态",
            description = "查看后端与 ComfyUI WebSocket 的连接状态、当前 sessionId 以及配置的 clientId。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
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
    @Operation(
            summary = "预览或下载生成图片",
            description = "根据 filename、type、subfolder 从 ComfyUI 拉取图片二进制流。该接口直接返回图片，不使用统一 Result 包装。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "图片二进制流", content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE, schema = @Schema(type = "string", format = "binary"))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public ResponseEntity<byte[]> previewImage(
            @Parameter(description = "图片文件名，通常来自历史记录 outputs.images.filename。", example = "ComfyUI_00001_.png", required = true)
            @RequestParam String filename,
            @Parameter(description = "图片类型，可选 output、input、temp；默认 output。", example = "output")
            @RequestParam(required = false, defaultValue = "output") String type,
            @Parameter(description = "图片所在子目录；没有子目录时传空字符串或不传。", example = "")
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
    @Operation(
            summary = "获取系统信息",
            description = "获取 ComfyUI 服务器系统信息，包括操作系统、Python 版本以及可用计算设备。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<SystemStatsResponse> getSystemStats() throws IOException {
        return Result.ok(comfyUiService.getSystemStats());
    }

    /** 获取指定节点的配置信息（输入/输出参数定义） */
    @GetMapping("/object-info/{nodeName}")
    @Operation(
            summary = "获取节点配置信息",
            description = "根据节点类型名称查询 ComfyUI 节点的输入、输出和默认参数定义。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Map<String, Object>> getObjectInfo(
            @Parameter(description = "ComfyUI 节点类型名称，如 KSampler、CLIPTextEncode、VAEDecode。", example = "KSampler")
            @PathVariable String nodeName) throws IOException {
        return Result.ok(comfyUiService.getObjectInfo(nodeName));
    }

    /** 取消当前正在执行的任务 */
    @PostMapping("/interrupt")
    @Operation(
            summary = "中断当前任务",
            description = "通知 ComfyUI 取消当前正在执行的任务。该接口无请求体。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "中断请求发送成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Void> interrupt() throws IOException {
        comfyUiService.interrupt();
        return Result.ok();
    }

    /** 获取当前任务队列状态（正在执行 + 等待执行） */
    @GetMapping("/queue")
    @Operation(
            summary = "获取任务队列",
            description = "获取 ComfyUI 当前正在执行和等待执行的任务队列。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<QueueResponse> getQueue() throws IOException {
        return Result.ok(comfyUiService.getQueue());
    }

    /** 从等待队列中删除指定任务，接收 prompt_id 列表 */
    @PostMapping("/queue/delete")
    @Operation(
            summary = "删除等待队列任务",
            description = "从 ComfyUI 等待队列中删除指定 prompt_id。当前后端接口接收 prompt_id 字符串数组，并转为 ComfyUI 的 {delete:[...]} 请求体。",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "要删除的 prompt_id 数组。",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(description = "任务 prompt_id。", example = "2f7a4d51-8c0e-4c2d-a4ef-1b2c3d4e5f60")),
                            examples = @ExampleObject(
                                    name = "删除队列任务示例",
                                    value = "[\"2f7a4d51-8c0e-4c2d-a4ef-1b2c3d4e5f60\",\"0be7a071-1c97-4dd7-8f19-a9b678f2eabc\"]"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "删除成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Void> deleteFromQueue(@RequestBody List<String> deleteIds) throws IOException {
        QueueDeleteRequest request = QueueDeleteRequest.builder()
                .delete(deleteIds)
                .build();
        comfyUiService.deleteFromQueue(request);
        return Result.ok();
    }

    /** 获取当前提示词配置信息 */
    @GetMapping("/prompt")
    @Operation(
            summary = "获取提示词状态",
            description = "获取 ComfyUI 当前 prompt 相关状态信息，通常包含节点版本、输出目录等运行时信息。",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Map<String, Object>> getPrompt() throws IOException {
        return Result.ok(comfyUiService.getPrompt());
    }

    /** 提交工作流任务到 ComfyUI，返回包含 prompt_id 和队列编号的响应 */
    @PostMapping("/prompt")
    @Operation(
            summary = "提交工作流任务",
            description = "提交 ComfyUI 工作流 JSON。请求体中的 prompt 必须是 ComfyUI API 兼容的节点图结构，后端会自动写入配置中的 clientId。",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "ComfyUI 工作流任务请求体。clientId 可不传；prompt 为必填工作流节点图。",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubmitTaskRequest.class),
                            examples = @ExampleObject(
                                    name = "提交工作流示例",
                                    value = "{\"clientId\":\"star-graph\",\"prompt\":{\"3\":{\"class_type\":\"KSampler\",\"inputs\":{\"seed\":123456,\"steps\":20,\"cfg\":8,\"sampler_name\":\"euler\",\"scheduler\":\"normal\",\"denoise\":1}},\"6\":{\"class_type\":\"CLIPTextEncode\",\"inputs\":{\"text\":\"a cinematic star graph, high detail\"}}}}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "提交成功，返回 prompt_id 和队列编号", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "400", description = "prompt 为空或请求体格式错误", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用或工作流提交失败", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
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
    @Operation(
            summary = "上传图片",
            description = "上传普通图片到 ComfyUI input 目录，可用于图生图、参考图等工作流。",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "multipart/form-data 请求体，字段名必须为 image。",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "object", description = "包含 image 文件字段的表单。")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用或上传失败", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Map<String, Object>> uploadImage(
            @Parameter(
                    description = "图片文件字段，字段名为 image，支持 ComfyUI 可识别的 png、jpg、webp 等格式。",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("image") MultipartFile file) throws IOException {
        MultipartBody.Part body = multipartBodyFactory.toImagePart(file);
        return Result.ok(comfyUiService.uploadImage(body));
    }

    /** 上传蒙版图片到 ComfyUI（用于 inpaint 等场景） */
    @PostMapping("/upload/mask")
    @Operation(
            summary = "上传蒙版图片",
            description = "上传蒙版图片到 ComfyUI，用于 inpaint 等需要 mask 的工作流。",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "multipart/form-data 请求体，image 必填，type、subfolder、originalRef 可选。",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "object", description = "包含 image 文件字段以及可选文本字段 type、subfolder、originalRef 的表单。")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未登录或 token 无效", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "502", description = "ComfyUI 服务不可用或上传失败", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Map<String, Object>> uploadMask(
            @Parameter(
                    description = "蒙版图片文件字段，字段名为 image。",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("image") MultipartFile file,
            @Parameter(description = "图片类型，通常为 input、output 或 temp；不传时由 ComfyUI 使用默认逻辑。", example = "input")
            @RequestParam(required = false) String type,
            @Parameter(description = "存储子目录；没有子目录时传空字符串或不传。", example = "")
            @RequestParam(required = false, defaultValue = "") String subfolder,
            @Parameter(description = "原始图片引用，ComfyUI mask 上传场景使用；可传原始图片文件名或对应引用 JSON 字符串。", example = "example.png")
            @RequestParam(required = false) String originalRef) throws IOException {
        MultipartBody.Part imagePart = multipartBodyFactory.toImagePart(file);
        okhttp3.RequestBody typeBody = multipartBodyFactory.toTextBody(type);
        okhttp3.RequestBody subfolderBody = multipartBodyFactory.toTextBody(subfolder);
        okhttp3.RequestBody originalRefBody = multipartBodyFactory.toTextBody(originalRef);
        return Result.ok(comfyUiService.uploadMask(imagePart, typeBody, subfolderBody, originalRefBody));
    }
}
