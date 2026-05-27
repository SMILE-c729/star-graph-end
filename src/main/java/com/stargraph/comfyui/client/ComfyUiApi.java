package com.stargraph.comfyui.client;

import com.stargraph.comfyui.model.PromptRequest;
import com.stargraph.comfyui.model.PromptResponse;
import com.stargraph.comfyui.model.QueueDeleteRequest;
import com.stargraph.comfyui.model.QueueResponse;
import com.stargraph.comfyui.model.SystemStatsResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Map;

/**
 * ComfyUI HTTP API 的 Retrofit 接口声明。
 * 每个方法对应 ComfyUI 服务器的一个 HTTP 端点，Retrofit 根据注解自动生成 HTTP 请求。
 *
 * 返回类型策略：
 * - 结构固定的接口使用强类型（SystemStatsResponse、QueueResponse、PromptResponse）
 * - 结构灵活/未固定的接口保留 Map&lt;String, Object&gt;
 * - 图片预览使用 ResponseBody 以支持流式读取
 */
public interface ComfyUiApi {

    /**
     * 获取历史记录列表
     * @param maxItems 最大返回条数，null 表示使用 ComfyUI 默认值
     */
    @GET("/history")
    Call<Map<String, Object>> getHistory(@Query("max_items") String maxItems);

    /**
     * 根据 prompt_id 获取单条历史记录
     * @param promptId 任务提交时返回的唯一标识
     */
    @GET("/history/{prompt_id}")
    Call<Map<String, Object>> getHistoryById(@Path("prompt_id") String promptId);

    /**
     * 预览/下载生成的图片，返回原始二进制流
     * @param filename 图片文件名
     * @param type 图片类型（output/input/temp）
     * @param subfolder 子目录
     */
    @GET("/view")
    Call<ResponseBody> previewImage(
            @Query("filename") String filename,
            @Query("type") String type,
            @Query("subfolder") String subfolder
    );

    /** 获取 ComfyUI 服务器系统信息（OS、Python、GPU 等） */
    @GET("/system_stats")
    Call<SystemStatsResponse> getSystemStats();

    /**
     * 获取指定节点的配置信息（输入/输出参数定义）
     * @param nodeName 节点类型名称，如 "KSampler"、"CLIPTextEncode"
     */
    @GET("/object_info/{nodeName}")
    Call<Map<String, Object>> getObjectInfo(@Path("nodeName") String nodeName);

    /** 取消当前正在执行的任务 */
    @POST("/interrupt")
    Call<Void> interrupt();

    /** 获取当前任务队列状态（正在执行 + 等待执行的任务列表） */
    @GET("/queue")
    Call<QueueResponse> getQueue();

    /**
     * 从等待队列中删除指定任务
     * @param request 包含要删除的 prompt_id 列表
     */
    @POST("/queue")
    Call<Void> deleteFromQueue(@Body QueueDeleteRequest request);

    /** 获取当前提示词配置信息（节点版本等） */
    @GET("/prompt")
    Call<Map<String, Object>> getPrompt();

    /**
     * 提交工作流任务到 ComfyUI
     * @param request 包含 client_id 和 prompt（工作流节点定义）
     * @return 包含 prompt_id 和队列编号的响应
     */
    @POST("/prompt")
    Call<PromptResponse> submitPrompt(@Body PromptRequest request);

    /**
     * 上传图片到 ComfyUI
     * @param image multipart 文件部分
     */
    @Multipart
    @POST("/upload/image")
    Call<Map<String, Object>> uploadImage(@Part MultipartBody.Part image);

    /**
     * 上传蒙版图片到 ComfyUI（用于 inpaint 等场景）
     * @param image 蒙版图片文件
     * @param type 图片类型
     * @param subfolder 存储子目录
     * @param originalRef 原始图片引用
     */
    @Multipart
    @POST("/upload/mask")
    Call<Map<String, Object>> uploadMask(
            @Part MultipartBody.Part image,
            @Part("type") RequestBody type,
            @Part("subfolder") RequestBody subfolder,
            @Part("original_ref") RequestBody originalRef
    );
}
