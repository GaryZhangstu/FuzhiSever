package com.fuzhi.fuzhiServer.util;

import cn.dev33.satoken.stp.StpUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("customKeyGenerator")
public class CustomKeyGenerator implements KeyGenerator {
    @NotNull
    @Override
    public String generate(@NotNull Object target, @NotNull Method method, @NotNull Object... params) {
        // 从 StpUtil 中获取 userId
        return StpUtil.getLoginId().toString(); // 返回 userId 作为缓存键
    }
}
