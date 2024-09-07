package cn.devcxl.ratelimit.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author devcxl
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /**
     * 次数
     *
     * @return
     */
    int max() default 1;

    /**
     * 时间
     *
     * @return
     */
    int time() default 5;

    /**
     * 时间单位
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示
     *
     * @return
     */
    String msg() default "系统繁忙，请稍后再试。";
}
