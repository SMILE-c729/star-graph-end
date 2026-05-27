package com.stargraph.comfyui.support;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * ComfyUI Multipart 请求体工厂。
 * 集中处理 Spring MultipartFile 到 OkHttp RequestBody 的转换，避免 Controller 重复关心底层 HTTP 细节。
 */
@Component
public class MultipartBodyFactory {

    private static final MediaType TEXT_PLAIN = MediaType.parse("text/plain");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/*";

    /**
     * 将上传文件转换为 ComfyUI 需要的 image 表单字段。
     */
    public MultipartBody.Part toImagePart(MultipartFile file) throws IOException {
        String contentType = file.getContentType() != null
                ? file.getContentType()
                : DEFAULT_IMAGE_CONTENT_TYPE;
        RequestBody requestFile = RequestBody.create(file.getBytes(), MediaType.parse(contentType));
        return MultipartBody.Part.createFormData("image", file.getOriginalFilename(), requestFile);
    }

    /**
     * 将可选文本字段转换为 text/plain 请求体。
     * ComfyUI multipart 接口接受空字符串，因此 null 会在这里统一归一化。
     */
    public RequestBody toTextBody(String value) {
        return RequestBody.create(value != null ? value : "", TEXT_PLAIN);
    }
}
