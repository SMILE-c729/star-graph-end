package com.stargraph.controller;

import com.stargraph.common.Result;
import com.stargraph.comfyui.model.PromptRequest;
import com.stargraph.comfyui.model.QueueDeleteRequest;
import com.stargraph.comfyui.service.ComfyUiService;
import com.stargraph.dto.SubmitTaskRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comfyui")
@RequiredArgsConstructor
public class ComfyUiController {

    private final ComfyUiService comfyUiService;

    @GetMapping("/history")
    public Result<Map<String, Object>> getHistory(
            @RequestParam(required = false) String maxItems) throws IOException {
        return Result.ok(comfyUiService.getHistory(maxItems));
    }

    @GetMapping("/history/{promptId}")
    public Result<Map<String, Object>> getHistoryById(@PathVariable String promptId) throws IOException {
        return Result.ok(comfyUiService.getHistoryById(promptId));
    }

    @GetMapping("/view")
    public ResponseEntity<byte[]> previewImage(
            @RequestParam String filename,
            @RequestParam(required = false, defaultValue = "output") String type,
            @RequestParam(required = false, defaultValue = "") String subfolder) throws IOException {
        try (ResponseBody responseBody = comfyUiService.previewImage(filename, type, subfolder)) {
            byte[] imageBytes = responseBody.bytes();
            String contentType = responseBody.contentType() != null
                    ? responseBody.contentType().toString()
                    : "image/png";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(imageBytes);
        }
    }

    @GetMapping("/system-stats")
    public Result<Map<String, Object>> getSystemStats() throws IOException {
        return Result.ok(comfyUiService.getSystemStats());
    }

    @GetMapping("/object-info/{nodeName}")
    public Result<Map<String, Object>> getObjectInfo(@PathVariable String nodeName) throws IOException {
        return Result.ok(comfyUiService.getObjectInfo(nodeName));
    }

    @PostMapping("/interrupt")
    public Result<Void> interrupt() throws IOException {
        comfyUiService.interrupt();
        return Result.ok();
    }

    @GetMapping("/queue")
    public Result<Map<String, Object>> getQueue() throws IOException {
        return Result.ok(comfyUiService.getQueue());
    }

    @PostMapping("/queue/delete")
    public Result<Void> deleteFromQueue(@org.springframework.web.bind.annotation.RequestBody List<String> deleteIds) throws IOException {
        QueueDeleteRequest request = new QueueDeleteRequest();
        request.setDelete(deleteIds);
        comfyUiService.deleteFromQueue(request);
        return Result.ok();
    }

    @GetMapping("/prompt")
    public Result<Map<String, Object>> getPrompt() throws IOException {
        return Result.ok(comfyUiService.getPrompt());
    }

    @PostMapping("/prompt")
    public Result<Map<String, Object>> submitPrompt(
            @Valid @org.springframework.web.bind.annotation.RequestBody SubmitTaskRequest request) throws IOException {
        PromptRequest promptRequest = new PromptRequest();
        promptRequest.setClientId(request.getClientId());
        promptRequest.setPrompt(request.getPrompt());
        return Result.ok(comfyUiService.submitPrompt(promptRequest));
    }

    @PostMapping("/upload/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(file.getBytes(),
                MediaType.parse(file.getContentType() != null ? file.getContentType() : "image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getOriginalFilename(), requestFile);
        return Result.ok(comfyUiService.uploadImage(body));
    }

    @PostMapping("/upload/mask")
    public Result<Map<String, Object>> uploadMask(
            @RequestParam("image") MultipartFile file,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "") String subfolder,
            @RequestParam(required = false) String originalRef) throws IOException {
        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(file.getBytes(),
                MediaType.parse(file.getContentType() != null ? file.getContentType() : "image/*"));
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getOriginalFilename(), requestFile);

        okhttp3.RequestBody typeBody = okhttp3.RequestBody.create(
                type != null ? type : "", MediaType.parse("text/plain"));
        okhttp3.RequestBody subfolderBody = okhttp3.RequestBody.create(
                subfolder != null ? subfolder : "", MediaType.parse("text/plain"));
        okhttp3.RequestBody originalRefBody = okhttp3.RequestBody.create(
                originalRef != null ? originalRef : "", MediaType.parse("text/plain"));

        return Result.ok(comfyUiService.uploadMask(imagePart, typeBody, subfolderBody, originalRefBody));
    }
}
