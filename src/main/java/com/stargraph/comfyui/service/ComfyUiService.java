package com.stargraph.comfyui.service;

import com.stargraph.comfyui.model.PromptRequest;
import com.stargraph.comfyui.model.PromptResponse;
import com.stargraph.comfyui.model.QueueDeleteRequest;
import com.stargraph.comfyui.model.QueueResponse;
import com.stargraph.comfyui.model.SystemStatsResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * ComfyUI HTTP 业务服务接口。
 * 统一定义后端可调用的 ComfyUI 能力，Controller 不直接依赖 Retrofit API。
 */
public interface ComfyUiService {

    /** 获取历史记录列表。 */
    Map<String, Object> getHistory(String maxItems) throws IOException;

    /** 根据 prompt_id 获取单条历史记录。 */
    Map<String, Object> getHistoryById(String promptId) throws IOException;

    /** 预览图片，返回原始 ResponseBody，调用方负责关闭流。 */
    ResponseBody previewImage(String filename, String type, String subfolder) throws IOException;

    /** 获取 ComfyUI 服务器系统信息。 */
    SystemStatsResponse getSystemStats() throws IOException;

    /** 获取指定节点的配置信息。 */
    Map<String, Object> getObjectInfo(String nodeName) throws IOException;

    /** 取消当前正在执行的任务。 */
    void interrupt() throws IOException;

    /** 获取当前任务队列状态。 */
    QueueResponse getQueue() throws IOException;

    /** 从等待队列中删除指定任务。 */
    void deleteFromQueue(QueueDeleteRequest request) throws IOException;

    /** 获取当前提示词配置信息。 */
    Map<String, Object> getPrompt() throws IOException;

    /** 提交工作流任务到 ComfyUI。 */
    PromptResponse submitPrompt(PromptRequest request) throws IOException;

    /** 上传图片到 ComfyUI。 */
    Map<String, Object> uploadImage(MultipartBody.Part image) throws IOException;

    /** 上传蒙版图片到 ComfyUI。 */
    Map<String, Object> uploadMask(MultipartBody.Part image, RequestBody type,
                                   RequestBody subfolder, RequestBody originalRef) throws IOException;
}
