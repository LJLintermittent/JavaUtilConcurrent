package com.duanxu.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:
 * date: 2021/8/19 23:33
 * Package: com.duanxu.lock
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class synchronizedTest {

    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock(false);
        lock.lock();
        lock.unlock();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    add2();
                }
            }, String.valueOf(i)).start();
        }
        Thread.sleep(1000);
        System.out.println(counter);
    }

//    public synchronized static void add1() {
//        counter++;
//    }

    public static void add2() {
        synchronized (synchronizedTest.class) {
            counter++;
        }
    }

}
