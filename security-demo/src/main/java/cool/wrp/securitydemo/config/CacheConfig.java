package cool.wrp.securitydemo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import cool.wrp.securitydemo.constant.JwtConstant;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.concurrent.TimeUnit;

/**
 * 配置缓存，使用 caffeine 做缓存
 *
 * @author 码小瑞
 */
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        final Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .initialCapacity(10)
                .expireAfterWrite(JwtConstant.EXPIRATION_TIME, TimeUnit.MINUTES);

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
