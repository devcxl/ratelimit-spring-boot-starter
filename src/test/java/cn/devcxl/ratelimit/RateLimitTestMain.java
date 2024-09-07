package cn.devcxl.ratelimit;

import cn.devcxl.ratelimit.annotation.RateLimit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RateLimitTestMain {

    public static void main(String[] args) {
        SpringApplication.run(RateLimitTestMain.class, args);
    }


    @GetMapping("/tests")
    @RateLimit
    public String test() {
        return "hello";
    }
}
