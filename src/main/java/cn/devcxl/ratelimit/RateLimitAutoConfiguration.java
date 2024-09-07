package cn.devcxl.ratelimit;

import cn.devcxl.ratelimit.interceptor.DefaultRateLimitInterceptor;
import cn.devcxl.ratelimit.interceptor.RateLimitInterceptor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author devcxl
 */
@Configuration
public class RateLimitAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RateLimitAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("RateLimitAutoConfiguration init...");
    }


    /**
     * RedisTemplate 配置
     *
     * @param lettuceConnectionFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    RedisTemplate<String, Integer> rateLimitRedisTemplate(RedisConnectionFactory lettuceConnectionFactory, RedisSerializer<Integer> rateLimitRedisSerializer) {
        RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(rateLimitRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(rateLimitRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * redis 序列化配置
     *
     * @return
     */
    @Bean
    public RedisSerializer<Integer> rateLimitRedisSerializer(ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Integer> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Integer.class);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        return serializer;
    }


    /**
     * 默认限速拦截器配置
     *
     * @param rateLimitRedisTemplate
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(RateLimitInterceptor.class)
    RateLimitInterceptor rateLimitInterceptor(RedisTemplate<String, Integer> rateLimitRedisTemplate) {
        return new DefaultRateLimitInterceptor(rateLimitRedisTemplate);
    }


    @Bean
    @ConditionalOnBean(RateLimitInterceptor.class)
    WebMvcConfigurer configurer(RateLimitInterceptor rateLimitInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(rateLimitInterceptor);
            }
        };
    }

}
