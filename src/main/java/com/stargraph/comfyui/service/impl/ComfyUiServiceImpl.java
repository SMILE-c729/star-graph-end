package com.stargraph.comfyui.service.impl;

import com.stargraph.common.constant.StarGraphConstant;
import com.stargraph.comfyui.client.ComfyUiApi;
import com.stargraph.comfyui.model.PromptRequest;
import com.stargraph.comfyui.model.PromptResponse;
import com.stargraph.comfyui.model.QueueDeleteRequest;
import com.stargraph.comfyui.model.QueueResponse;
import com.stargraph.comfyui.model.SystemStatsResponse;
import com.stargraph.comfyui.service.ComfyUiService;
import com.stargraph.comfyui.support.RetrofitCallExecutor;
import lombok.RequiredArgsConstructor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * ComfyUI 业务服务层。
 * 封装 ComfyUiApi 的 Retrofit 调用，统一处理同步执行和错误处理逻辑。
 * 所有方法均为同步调用，由 Spring Boot 内嵌 Tomcat 线程池管理并发。
 * Controller 层应通过此服务间接访问 ComfyUI，而非直接使用 ComfyUiApi。
 */
@Service
@RequiredArgsConstructor
public class ComfyUiServiceImpl implements ComfyUiService {

    private final ComfyUiApi comfyUiApi;
    private final RetrofitCallExecutor callExecutor;

    /** 获取历史记录列表 */
    @Override
    public Map<String, Object> getHistory(String maxItems) throws IOException {
        return callExecutor.execute(comfyUiApi.getHistory(maxItems),
                StarGraphConstant.ErrorMessage.ComfyUi.GET_HISTORY);
    }

    /** 根据 prompt_id 获取单条历史记录 */
    @Override
    public Map<String, Object> getHistoryById(String promptId) throws IOException {
        return callExecutor.execute(comfyUiApi.getHistoryById(promptId),
                StarGraphConstant.ErrorMessage.ComfyUi.GET_HISTORY_DETAIL);
    }

    /**
     * 预览图片，返回原始 ResponseBody（调用方需负责关闭流）。
     * ResponseBody 需要交给 Controller 读取并关闭，所以这里只做成功判断，不提前消费响应流。
     */
    @Override
    public ResponseBody previewImage(String filename, String type, String subfolder) throws IOException {
        return callExecutor.execute(
                comfyUiApi.previewImage(filename, type, subfolder),
                StarGraphConstant.ErrorMessage.ComfyUi.PREVIEW_IMAGE
        );
    }

    /** 获取 ComfyUI 服务器系统信息（OS、Python、GPU 等） */
    @Override
    public SystemStatsResponse getSystemStats() throws IOException {
        return callExecutor.execute(comfyUiApi.getSystemStats(),
                StarGraphConstant.ErrorMessage.ComfyUi.GET_SYSTEM_STATS);
    }

    /** 获取指定节点的配置信息 */
    @Override
    public Map<String, Object> getObjectInfo(String nodeName) throws IOException {
        return callExecutor.execute(comfyUiApi.getObjectInfo(nodeName),
                StarGraphConstant.ErrorMessage.ComfyUi.GET_OBJECT_INFO);
    }

    /** 取消当前正在执行的任务 */
    @Override
    public void interrupt() throws IOException {
        callExecutor.executeVoid(comfyUiApi.interrupt(), StarGraphConstant.ErrorMessage.ComfyUi.INTERRUPT);
    }

    /** 获取当前任务队列状态 */
    @Override
    public QueueResponse getQueue() throws IOException {
        return callExecutor.execute(comfyUiApi.getQueue(), StarGraphConstant.ErrorMessage.ComfyUi.GET_QUEUE);
    }

    /** 从等待队列中删除指定任务 */
    @Override
    public void deleteFromQueue(QueueDeleteRequest request) throws IOException {
        callExecutor.executeVoid(comfyUiApi.deleteFromQueue(request),
                StarGraphConstant.ErrorMessage.ComfyUi.DELETE_QUEUE);
    }

    /** 获取当前提示词配置信息 */
    @Override
    public Map<String, Object> getPrompt() throws IOException {
        return callExecutor.execute(comfyUiApi.getPrompt(), StarGraphConstant.ErrorMessage.ComfyUi.GET_PROMPT);
    }

    /** 提交工作流任务到 ComfyUI，返回包含 prompt_id 的响应 */
    @Override
    public PromptResponse submitPrompt(PromptRequest request) throws IOException {
        return callExecutor.execute(comfyUiApi.submitPrompt(request),
                StarGraphConstant.ErrorMessage.ComfyUi.SUBMIT_PROMPT);
    }

    /** 上传图片到 ComfyUI */
    @Override
    public Map<String, Object> uploadImage(MultipartBody.Part image) throws IOException {
        return callExecutor.execute(comfyUiApi.uploadImage(image),
                StarGraphConstant.ErrorMessage.ComfyUi.UPLOAD_IMAGE);
    }

    /** 上传蒙版图片到 ComfyUI */
    @Override
    public Map<String, Object> uploadMask(MultipartBody.Part image, RequestBody type,
                                           RequestBody subfolder, RequestBody originalRef) throws IOException {
        return callExecutor.execute(comfyUiApi.uploadMask(image, type, subfolder, originalRef),
                StarGraphConstant.ErrorMessage.ComfyUi.UPLOAD_MASK);
    }
}
