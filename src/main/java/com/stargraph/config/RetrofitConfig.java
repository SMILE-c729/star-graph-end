package com.stargraph.config;

import com.stargraph.comfyui.client.ComfyUiApi;
import com.stargraph.comfyui.properties.ComfyUiProperties;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Retrofit HTTP 客户端配置类。
 * 构建 OkHttpClient → Retrofit → ComfyUiApi 三级 Bean 注册链。
 * Retrofit 负责将 Java 接口方法声明转换为 HTTP 请求，OkHttp 作为底层 HTTP 引擎。
 */
@Configuration
public class RetrofitConfig {

    /** 连接超时 30s，读写超时 60s（ComfyUI 生成图片可能耗时较长） */
    @Bean
    public OkHttpClient okHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    /** Retrofit 实例，baseUrl 从 application.yaml 的 comfyui.base-url 读取 */
    @Bean
    public Retrofit retrofit(OkHttpClient okHttpClient, ComfyUiProperties properties) {
        return new Retrofit.Builder()
                .baseUrl(properties.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())  // 使用 Gson 做 JSON 序列化/反序列化
                .build();
    }

    /** ComfyUiApi 动态代理，Retrofit 根据接口注解自动生成 HTTP 调用实现 */
    @Bean
    public ComfyUiApi comfyUiApi(Retrofit retrofit) {
        return retrofit.create(ComfyUiApi.class);
    }
}
