package com.nuofankj.caffeinestudy.load;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author xifanxiaxue
 * @date 2020/11/19 22:34
 * @desc 异步加载
 */
public class CaffeineAsynchronousTest {

    /**
     * 模拟从数据库中读取key
     *
     * @param key
     * @return
     */
    private int getInDB(int key) {
        return key + 1;
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        // 使用executor设置线程池
        AsyncCache<Integer, Integer> asyncCache = Caffeine.newBuilder()
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
}
