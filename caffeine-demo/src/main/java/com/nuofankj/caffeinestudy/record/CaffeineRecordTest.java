package com.nuofankj.caffeinestudy.record;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

/**
 * @author xifanxiaxue
 * @date 2020/12/1 23:12
 * @desc
 */
public class CaffeineRecordTest {

    /**
     * 模拟从数据库中读取数据
     *
     * @param key
     * @return
     */
    private int getInDB(int key) {
        return key;
    }

    @Test
    public void test() {
        LoadingCache<Integer, Integer> cache = Caffeine.newBuilder()
                // 开启记录
                .recordStats()
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) {
                        return getInDB(key);
                    }
                });
        cache.get(1);
        cache.get(1);

        // 命中率
        System.out.println(cache.stats().hitRate());
        // 被剔除的数量
        System.out.println(cache.stats().evictionCount());
        // 加载新值所花费的平均时间[纳秒]
        System.out.println(cache.stats().averageLoadPenalty() );
    }
}
