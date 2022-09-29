package cool.wrp.securitydemo.constant;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 码小瑞
 */
@AllArgsConstructor
public enum CacheName {

    /**
     * 用户
     */
    USER("USER"),

    /**
     * 权限
     */
    PERMISSION("PERMISSION");

    private final String cacheName;

    public static List<String> getCacheNames() {
        List<String> cacheNameList = new ArrayList<>(CacheName.values().length);
        CacheName[] values = CacheName.values();
        for (int i = 0; i < CacheName.values().length; i++) {
            cacheNameList.add(values[i].cacheName);
        }
        return cacheNameList;
    }

    public String getCacheName() {
        return cacheName;
    }
}
