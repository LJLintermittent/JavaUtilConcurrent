package com.duanxu.vm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description:
 * date: 2021/8/30 12:43
 * Package: com.duanxu.vm
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("All")
public class OOMTest2 {
    public static void main(String[] args) throws InterruptedException {


        //当一个线程OOM以后，jvm堆内存空间抖动，证明释放掉了线程的内存资源，但是不影响其他线程的运行
        //发生oom的线程一般情况下会死亡，也就是被终结了
        Thread.sleep(15000);
        new Thread(() -> {
            List<byte[]> bytes = new ArrayList<>();
            while (true) {
                System.out.println(new Date().toString() + Thread.currentThread().getName());
                byte[] nums = new byte[1024 * 1024 * 1024];
                bytes.add(nums);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                System.out.println(new Date().toString() + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }
}
