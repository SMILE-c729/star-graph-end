package com.stargraph.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类。
 * 注册分页拦截器，使 Mapper 的 selectPage 方法能自动生成 MySQL 分页 SQL。
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器链。
     * 添加 MySQL 分页拦截器后，调用 Page 对象进行查询时会自动拼接 LIMIT 子句。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 指定数据库类型为 MySQL，分页插件会生成对应的 LIMIT 语法
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
