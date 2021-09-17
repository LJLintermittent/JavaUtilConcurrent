package com.duanxu.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * date: 2021/9/17 16:09
 * Package: com.duanxu.thread
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class TwinsLockTest {

    public static void main(String[] args) {
        TwinsLock lock = new TwinsLock(2);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }).start();
        }

    }
}
