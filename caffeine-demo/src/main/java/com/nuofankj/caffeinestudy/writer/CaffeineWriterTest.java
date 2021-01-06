package com.nuofankj.caffeinestudy.writer;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xifanxiaxue
 * @date 2020/12/5 10:18
 * @desc
 */
public class CaffeineWriterTest {

    /**
     * 充当二级缓存用，生命周期仅活到下个gc
     */
    private Map<Integer, WeakReference<Integer>> secondCacheMap =
            new ConcurrentHashMap<>();

    @Test
    public void test() throws InterruptedException {
        // 设置最大缓存个数为1
        LoadingCache<Integer, Integer> cache = Caffeine.newBuilder()
                .maximumSize(1)
                // 设置put和remove的回调
                .writer(new CacheWriter<Integer, Integer>() {
                    @Override
                    public void write(@NonNull Integer key, @NonNull Integer value) {
                        secondCacheMap.put(key, new WeakReference<>(value));
                        System.out.println("触发CacheWriter.write，将key = " + key + "放入二级缓存中");
                    }

                    @Override
                    public void delete(@NonNull Integer key, @Nullable Integer value, @NonNull RemovalCause cause) {
                        switch (cause) {
                            case EXPLICIT:
                                secondCacheMap.remove(key);
                                System.out.println("触发CacheWriter" +
                                        ".delete，清除原因：主动清除，将key = " + key +
                                        "从二级缓存清除");
                                break;
                            case SIZE:
                                System.out.println("触发CacheWriter" +
                                        ".delete，清除原因：缓存个数超过上限，key = " + key);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) {
                        WeakReference<Integer> value = secondCacheMap.get(key);
                        if (value == null) {
                            return null;
                        }

                        System.out.println("触发CacheLoader.load，从二级缓存读取key = " + key);
                        return value.get();
                    }
                });

        cache.put(1, 1);
        cache.put(2, 2);
        // 由于清除缓存是异步的，因而睡眠1秒等待清除完成
        Thread.sleep(1000);

        // 缓存超上限触发清除后
        System.out.println("从Caffeine中get数据，key为1，value为"+cache.get(1));
    }
}
