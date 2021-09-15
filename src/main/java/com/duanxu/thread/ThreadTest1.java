package com.duanxu.thread;

/**
 * Description:
 * date: 2021/9/15 20:20
 * Package: com.duanxu.thread
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class ThreadTest1 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("我是线程");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            System.out.println("我先跑了");

        });
        thread1.start();
        thread2.start();
        //主线程执行了join方法，表示主线程会等待thread1线程执行完了以后才会返回，也就是才会轮到自己执行
        // join还提供了超时等待方法，如果调用join的线程没有在限定的时间内执行完毕，那么当前线程会直接返回，开始执行自己的流程
        thread1.join(2000);
        System.out.println("hahahha ");
    }
}
