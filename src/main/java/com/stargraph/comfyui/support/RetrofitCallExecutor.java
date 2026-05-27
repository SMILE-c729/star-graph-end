package com.stargraph.comfyui.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

/**
 * Retrofit 同步调用执行器。
 * 将 HTTP 成功判断、错误体读取和日志记录集中处理，避免各个 Service 重复编写样板代码。
 */
@Slf4j
@Component
public class RetrofitCallExecutor {

    /**
     * 执行带响应体的 Retrofit 调用。
     * 成功时返回 body；失败或空 body 时抛出 IOException，由全局异常处理器转为统一响应。
     */
    public <T> T execute(Call<T> call, String failureMessage) throws IOException {
        Response<T> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        }

        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
        log.error("{}: HTTP {}, URL: {}, Error: {}",
                failureMessage, response.code(), call.request().url(), errorBody);
        throw new IOException(failureMessage + ": HTTP " + response.code() + " - " + errorBody);
    }

    /**
     * 执行无响应体的 Retrofit 调用。
     * ComfyUI 的 interrupt、queue delete 等接口只需要根据 HTTP 状态判断是否成功。
     */
    public void executeVoid(Call<Void> call, String failureMessage) throws IOException {
        Response<Void> response = call.execute();
        if (response.isSuccessful()) {
            return;
        }

        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
        log.error("{}: HTTP {}, URL: {}, Error: {}",
                failureMessage, response.code(), call.request().url(), errorBody);
        throw new IOException(failureMessage + ": HTTP " + response.code() + " - " + errorBody);
    }
}
