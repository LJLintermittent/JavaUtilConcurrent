package com.duanxu.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * date: 2021/8/17 22:25
 * Package: com.duanxu.threadpool
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class threadpoolTest {
    public static void main(String[] args) {

        // 阻塞队列大小为integer.MAX_VALUE
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        // 最大线程数为Integer.MAX_VALUE
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        // 最大线程数为Integer.MAX_VALUE
        ExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
        // 阻塞队列大小为Integer.MAX_VALUE
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    }
}
