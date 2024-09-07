package cn.devcxl.ratelimit.interceptor;


import cn.devcxl.ratelimit.annotation.RateLimit;
import cn.devcxl.ratelimit.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


/**
 * 接口限流
 *
 * @author devcxl
 */
@Component
public abstract class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Integer> redisTemplate;

    public RateLimitInterceptor(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检查API限制
     *
     * @param rateLimit 注解
     * @param limitKey  RedisKey
     * @return 是否拦截
     */
    private boolean checkLimit(RateLimit rateLimit, String limitKey) {
        int max = rateLimit.max();
        int time = rateLimit.time();
        TimeUnit timeUnit = rateLimit.timeUnit();
        Integer count = redisTemplate.opsForValue().get(limitKey);
        if (count != null) {
            if (count < max) {
                Long expire = redisTemplate.getExpire(limitKey);
                if (expire != null && expire <= 0) {
                    redisTemplate.opsForValue().set(limitKey, 1, time, timeUnit);
                } else {
                    redisTemplate.opsForValue().set(limitKey, ++count, time, timeUnit);
                }
            } else {
                throw new RateLimitException(rateLimit.msg());
            }
        } else {
            redisTemplate.opsForValue().set(limitKey, 1, time, timeUnit);
        }
        return true;
    }

    public abstract String setLimitKey(Method method, HttpServletRequest request);

    /**
     * default preHandle
     *
     * @param request  请求
     * @param response 返回
     * @param handler  请求方法
     * @return 是否拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            String name = method.getName();
            RateLimit rateLimit = method.getAnnotation(RateLimit.class);
            if (rateLimit == null) {
                return true;
            } else {
                return checkLimit(rateLimit, setLimitKey(method, request));
            }
        } else {
            return false;
        }
    }

}
