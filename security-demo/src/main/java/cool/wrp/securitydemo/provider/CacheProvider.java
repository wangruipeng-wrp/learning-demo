package cool.wrp.securitydemo.provider;

import cool.wrp.securitydemo.constant.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 缓存工具类
 *
 * @author 码小瑞
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CacheProvider {

    private final CacheManager caffeineCacheManager;

    public <T> T get(CacheName cacheName, String key, Class<T> clazz) {
        log.info("{} get -> cacheName [{}], key [{}], class type [{}]",
                this.getClass().getName(),
                cacheName, key, clazz.getName());

        final Cache cache = caffeineCacheManager.getCache(cacheName.getCacheName());
        Assert.notNull(cache, "cache can not be null.");
        return cache.get(key, clazz);
    }

    public void put(CacheName cacheName, String key, Object value) {
        log.info("{} put -> cacheName [{}], key [{}], class type [{}]",
                this.getClass().getName(),
                cacheName, key, value);

        final Cache cache = caffeineCacheManager.getCache(cacheName.getCacheName());
        Assert.notNull(cache, "cache can not be null.");
        cache.put(key, value);
    }

    public void remove(CacheName cacheName, String key) {
        log.info("{} remove -> cacheName [{}], key [{}]",
                this.getClass().getName(),
                cacheName, key
        );

        final Cache cache = caffeineCacheManager.getCache(cacheName.getCacheName());
        Assert.notNull(cache, "cache can not be null.");
        cache.evict(key);
    }
}
