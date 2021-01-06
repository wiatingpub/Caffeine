package com.nuofankj.caffeinestudy.eviction;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Test;

/**
 * @author xifanxiaxue
 * @date 2020/11/19 22:34
 * @desc 基于大小淘汰
 */
public class CaffeineSizeEvictionTest {

    @Test
    public  void test() throws InterruptedException {
        // 初始化缓存，缓存最大个数为1
        Cache<Integer, Integer> cache = Caffeine.newBuilder()
                .maximumSize(1)
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
