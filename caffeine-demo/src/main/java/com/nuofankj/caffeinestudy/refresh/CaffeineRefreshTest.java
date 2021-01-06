package com.nuofankj.caffeinestudy.refresh;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author xifanxiaxue
 * @date 2020/12/1 23:12
 * @desc
 */
public class CaffeineRefreshTest {

    private int index = 1;

    /**
     * 模拟从数据库中读取数据
     *
     * @return
     */
    private int getInDB() {
        // 这里为了体现数据重新被get，因而用了index++
        index++;
        return index;
    }

    @Test
    public void test() throws InterruptedException {
        // 设置写入后3秒后数据过期，2秒后如果有数据访问则刷新数据
        LoadingCache<Integer, Integer> cache = Caffeine.newBuilder()
                .refreshAfterWrite(2, TimeUnit.SECONDS)
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) {
                        return getInDB();
                    }
                });
        cache.put(1, getInDB());

        // 休眠2.5秒，后取值
        Thread.sleep(2500);
        System.out.println(cache.getIfPresent(1));

        // 休眠1.5秒，后取值
        Thread.sleep(1500);
        System.out.println(cache.getIfPresent(1));
    }
}
