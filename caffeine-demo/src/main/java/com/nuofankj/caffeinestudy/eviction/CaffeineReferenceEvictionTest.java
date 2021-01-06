package com.nuofankj.caffeinestudy.eviction;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author xifanxiaxue
 * @date 2020/11/25 0:43
 * @desc 基于引用淘汰
 */
public class CaffeineReferenceEvictionTest {

    @Test
    public void testWeak() {
        Cache<Integer, Integer> cache = Caffeine.newBuilder()
                // 设置Key为弱引用，生命周期是下次gc的时候
                .weakKeys()
                // 设置value为弱引用，生命周期是下次gc的时候
                .weakValues()
                .build();
        cache.put(1, 2);
        System.out.println(cache.getIfPresent(1));

        // 强行调用一次GC
        System.gc();

        System.out.println(cache.getIfPresent(1));
    }

    @Test
    public void testSoft() {
        Cache<Integer, Integer> cache = Caffeine.newBuilder()
                // 设置value为软引用，生命周期是GC时并且堆内存不够时触发清除
                .softValues()
                .build();
        cache.put(1, 2);
        System.out.println(cache.getIfPresent(1));

        // 强行调用一次GC
        System.gc();

        System.out.println(cache.getIfPresent(1));
    }

    @Test
    public void testReferenceAsy() throws ExecutionException, InterruptedException {
        // 使用executor设置线程池
        AsyncCache<Integer, Integer> asyncCache = Caffeine.newBuilder()
                // 设置Key为弱引用，生命周期是下次gc的时候
                .weakKeys()
                // 设置value为弱引用，生命周期是下次gc的时候
                .weakValues()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100).executor(Executors.newSingleThreadExecutor()).buildAsync();
        Integer key = 1;
        // get返回的是CompletableFuture
        CompletableFuture<Integer> future = asyncCache.get(key, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer key) {
                // 执行所在的线程不在是main，而是ForkJoinPool线程池提供的线程
                System.out.println("当前所在线程：" + Thread.currentThread().getName());
                int value = getInDB(key);
                return value;
            }
        });

        int value = future.get();
        System.out.println("当前所在线程：" + Thread.currentThread().getName());
        System.out.println(value);
    }

    /**
     * 模拟从数据库中读取key
     *
     * @param key
     * @return
     */
    private int getInDB(int key) {
        return key + 1;
    }
}
