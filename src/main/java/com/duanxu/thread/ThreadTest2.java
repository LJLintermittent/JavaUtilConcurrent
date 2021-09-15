package com.duanxu.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * date: 2021/9/15 20:50
 * Package: com.duanxu.thread
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class ThreadTest2 {

    public static void main(String[] args) throws InterruptedException {
        Profiler.begin();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("耗时：" + Profiler.consum() + "ms");


    }

}
