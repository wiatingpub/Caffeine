package com.nuofankj.caffeinestudy.eviction;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;

/**
 * @author xifanxiaxue
 * @date 2020/11/21 15:26
 * @desc 基于缓存权重
 */
public class CaffeineWeightEvictionTest {

    @Test
    public void test() throws InterruptedException {
        // 初始化缓存，设置最大权重为2
        Cache<Integer, Integer> cache = Caffeine.newBuilder()
                .maximumWeight(2)
                .weigher(new Weigher<Integer, Integer>() {
                    @Override
                    public @NonNegative int weigh(@NonNull Integer key, @NonNull Integer value) {
                        return key;
                    }
                })
                .build();

        cache.put(1, 1);
        // 打印缓存个数，结果为1
        System.out.println(cache.estimatedSize());

        cache.put(2, 2);
        // 稍微休眠一秒
        Thread.sleep(1000);
        // 打印缓存个数，结果为1
        System.out.println(cache.estimatedSize());
    }
}
