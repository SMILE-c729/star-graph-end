package com.stargraph.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stargraph.common.constant.StarGraphConstant;
import com.stargraph.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 登录认证拦截器。
 * 从请求头提取 JWT，校验通过后写入 UserContext，供后续业务代码获取当前登录用户。
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final ObjectMapper objectMapper;

    /**
     * 预处理方法，在 Controller 方法调用之前执行。
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  处理器对象
     * @return 如果返回 true，则继续执行后续的拦截器和目标方法；如果返回 false，则中断后续的拦截器和目标方法的执行。
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 预检请求直接通过
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 认证通过后把用户信息放入线程上下文，当前请求处理链都可以安全读取。
        String token = resolveToken(request);
        try {
            UserContext.set(JwtTokenUtil.parseToken(token));
            return true;
        } catch (IllegalArgumentException e) {
            writeUnauthorized(response, e.getMessage());
            return false;
        }
    }

    /**
     * 请求处理完成，线程上下文清理。
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  处理器对象
     * @param ex       异常对象
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
    /**
     * 从请求头中解析 JWT Token。
     * @param request HTTP 请求对象
     * @return JWT Token 字符串，如果请求头中没有包含 JWT Token，则返回 null。
     */
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization)) {
            if (authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
                return authorization.substring(BEARER_PREFIX.length()).trim();
            }
            return authorization.trim();
        }

        String token = request.getHeader("token");
        if (StringUtils.hasText(token)) {
            return token.trim();
        }
        String xToken = request.getHeader("X-Token");
        return StringUtils.hasText(xToken) ? xToken.trim() : null;
    }

    /**
     * 认证失败时直接写出统一 JSON 响应，避免进入 Controller。
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(StarGraphConstant.ResponseCode.UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.fail(StarGraphConstant.ResponseCode.UNAUTHORIZED, message)));
    }
}
