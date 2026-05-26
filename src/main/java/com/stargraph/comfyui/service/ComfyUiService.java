package com.stargraph.comfyui.service;

import com.stargraph.comfyui.client.ComfyUiApi;
import com.stargraph.comfyui.model.PromptRequest;
import com.stargraph.comfyui.model.QueueDeleteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComfyUiService {

    private final ComfyUiApi comfyUiApi;

    public Map<String, Object> getHistory(String maxItems) throws IOException {
        return executeCall(comfyUiApi.getHistory(maxItems));
    }

    public Map<String, Object> getHistoryById(String promptId) throws IOException {
        return executeCall(comfyUiApi.getHistoryById(promptId));
    }

    public ResponseBody previewImage(String filename, String type, String subfolder) throws IOException {
        Response<ResponseBody> response = comfyUiApi.previewImage(filename, type, subfolder).execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        }
        throw new IOException("预览图片失败: HTTP " + response.code());
    }

    public Map<String, Object> getSystemStats() throws IOException {
        return executeCall(comfyUiApi.getSystemStats());
    }

    public Map<String, Object> getObjectInfo(String nodeName) throws IOException {
        return executeCall(comfyUiApi.getObjectInfo(nodeName));
    }

    public void interrupt() throws IOException {
        executeVoid(comfyUiApi.interrupt());
    }

    public Map<String, Object> getQueue() throws IOException {
        return executeCall(comfyUiApi.getQueue());
    }

    public void deleteFromQueue(QueueDeleteRequest request) throws IOException {
        executeVoid(comfyUiApi.deleteFromQueue(request));
    }

    public Map<String, Object> getPrompt() throws IOException {
        return executeCall(comfyUiApi.getPrompt());
    }

    public Map<String, Object> submitPrompt(PromptRequest request) throws IOException {
        return executeCall(comfyUiApi.submitPrompt(request));
    }

    public Map<String, Object> uploadImage(MultipartBody.Part image) throws IOException {
        return executeCall(comfyUiApi.uploadImage(image));
    }

    public Map<String, Object> uploadMask(MultipartBody.Part image, RequestBody type,
                                           RequestBody subfolder, RequestBody originalRef) throws IOException {
        return executeCall(comfyUiApi.uploadMask(image, type, subfolder, originalRef));
    }

    private <T> T executeCall(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        }
        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
        log.error("ComfyUI 请求失败: HTTP {}, URL: {}, Error: {}", response.code(), call.request().url(), errorBody);
        throw new IOException("ComfyUI 请求失败: HTTP " + response.code() + " - " + errorBody);
    }

    private void executeVoid(Call<Void> call) throws IOException {
        Response<Void> response = call.execute();
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            log.error("ComfyUI 请求失败: HTTP {}, URL: {}, Error: {}", response.code(), call.request().url(), errorBody);
            throw new IOException("ComfyUI 请求失败: HTTP " + response.code() + " - " + errorBody);
        }
    }
}
