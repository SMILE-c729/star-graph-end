package com.stargraph.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 头像上传响应。
 */
@Data
@AllArgsConstructor
@Schema(name = "AvatarUploadResponse", description = "头像上传响应。")
public class AvatarUploadResponse {

    /** 头像公共访问地址 */
    @Schema(description = "头像公共访问地址。")
    private String avatar;
}
