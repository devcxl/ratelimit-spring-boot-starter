package cn.devcxl.ratelimit.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;

/**
 * @author devcxl
 */
public class DefaultRateLimitInterceptor extends RateLimitInterceptor {

    /**
     * redisKey模板
     */
    private static final String LIMIT_KEY_TEMPLATE = "rate_limit_%s_%s_%s";


    public DefaultRateLimitInterceptor(RedisTemplate<String, Integer> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public String setLimitKey(Method method, HttpServletRequest request) {
        String functionName = method.getName();
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();
        return String.format(LIMIT_KEY_TEMPLATE, functionName, uri, httpMethod);
    }
}
