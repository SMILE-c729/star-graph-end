package com.stargraph.auth;

/**
 * 当前请求用户上下文。
 * 基于 ThreadLocal 保存登录用户信息，请求结束后必须清理，避免线程复用造成用户串号。
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    /**
     * 写入当前请求的登录用户。
     */
    public static void set(LoginUser user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取当前请求的登录用户。
     */
    public static LoginUser get() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        LoginUser user = get();
        return user == null ? null : user.getId();
    }

    public static String getUsername() {
        LoginUser user = get();
        return user == null ? null : user.getUsername();
    }

    /**
     * 清理当前线程保存的登录用户。
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}
