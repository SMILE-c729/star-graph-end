package com.stargraph.comfyui.client;

import com.stargraph.comfyui.model.PromptRequest;
import com.stargraph.comfyui.model.QueueDeleteRequest;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface ComfyUiApi {

    /**
     * 获取历史记录
     */
    @GET("/history")
    Call<Map<String, Object>> getHistory(@Query("max_items") String maxItems);

    /**
     * 获取某条历史记录
     */
    @GET("/history/{prompt_id}")
    Call<Map<String, Object>> getHistoryById(@Path("prompt_id") String promptId);

    /**
     * 预览图片
     */
    @GET("/view")
    Call<ResponseBody> previewImage(
            @Query("filename") String filename,
            @Query("type") String type,
            @Query("subfolder") String subfolder
    );

    /**
     * 获取系统信息
     */
    @GET("/system_stats")
    Call<Map<String, Object>> getSystemStats();

    /**
     * 获取节点配置信息
     */
    @GET("/object_info/{nodeName}")
    Call<Map<String, Object>> getObjectInfo(@Path("nodeName") String nodeName);

    /**
     * 取消当前执行
     */
    @POST("/interrupt")
    Call<Void> interrupt();

    /**
     * 获取队列任务列表
     */
    @GET("/queue")
    Call<Map<String, Object>> getQueue();

    /**
     * 删除队列中的任务
     */
    @POST("/queue")
    Call<Void> deleteFromQueue(@Body QueueDeleteRequest request);

    /**
     * 获取当前提示词信息
     */
    @GET("/prompt")
    Call<Map<String, Object>> getPrompt();

    /**
     * 提交提示词任务
     */
    @POST("/prompt")
    Call<Map<String, Object>> submitPrompt(@Body PromptRequest request);

    /**
     * 上传图片
     */
    @Multipart
    @POST("/upload/image")
    Call<Map<String, Object>> uploadImage(@Part MultipartBody.Part image);

    /**
     * 上传蒙版
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
