package com.stargraph.config;

import com.stargraph.comfyui.client.ComfyUiApi;
import com.stargraph.comfyui.config.ComfyUiProperties;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public Retrofit retrofit(OkHttpClient okHttpClient, ComfyUiProperties properties) {
        return new Retrofit.Builder()
                .baseUrl(properties.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Bean
    public ComfyUiApi comfyUiApi(Retrofit retrofit) {
        return retrofit.create(ComfyUiApi.class);
    }
}
