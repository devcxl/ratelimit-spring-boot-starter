package cn.devcxl.ratelimit.exception;

/**
 * @author devcxl
 */
public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
