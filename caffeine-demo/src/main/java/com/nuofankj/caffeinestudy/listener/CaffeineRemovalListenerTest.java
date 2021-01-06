package com.nuofankj.caffeinestudy.listener;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.C;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author xifanxiaxue
 * @date 2020/11/19 22:34
 * @desc 淘汰通知
 */
public class CaffeineRemovalListenerTest {

    @Test
    public void test() throws InterruptedException {
        LoadingCache<Integer, Integer> cache = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.SECONDS)
                .scheduler(Scheduler.systemScheduler())
                .removalListener(((key, value, cause) -> {
                    System.out.println("淘汰通知，key：" + key + "，原因：" + cause);
                }))
                .writer(new CacheWriter<Integer, Integer>() {
                    @Override
                    public void write(@NonNull Integer key, @NonNull Integer value) {

                    }

                    @Override
                    public void delete(@NonNull Integer key, @Nullable Integer value, @NonNull RemovalCause cause) {

                    }
                })
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        return key;
                    }
                });

        cache.put(1, 2);

        Thread.currentThread().sleep(2000);
    }
}
