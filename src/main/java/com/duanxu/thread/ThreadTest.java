package com.duanxu.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Description:
 * date: 2021/8/17 22:00
 * Package: com.duanxu.thread
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class ThreadTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println(Thread.currentThread().getName() + "运行");
        // 由于Thread1继承了Thread类，所以直接new Thread1().start()启动是可以的;
//        Thread1 thread1 = new Thread1();
//        thread1.start();
//        new Thread(new Thread2()).start();
        // FutureTask实现了RunnableFuture接口，而RunnableFuture接口又继承了Runnable接口
        // 所以，它把一个Callable接口的实现类包装成了一个Runnable接口的实现类
//        FutureTask<Integer> futureTask = new FutureTask<>(new Thread3());
//        new Thread(futureTask).start();
//        Integer integer = futureTask.get();
//        System.out.println("futureTask返回结果：" + integer);
        // FutureTask不仅可以传一个Callable，还可以传一个Runnable接口的实现类外加一个对象
        // 但是这个对象只能在线程创建之前传进去，在run方法里面是无法获取到这个结果的，也没有办法对这个结果进行改造
        // 从而让run方法也有类似call方法的返回值，这是无法实现的。
        User user = new User();
        user.setName("李佳乐在阿里巴巴");
        user.setAge(21);
        FutureTask<User> futureTask = new FutureTask<>(new Thread2(), user);
        new Thread(futureTask).start();
        User ansUser = futureTask.get();
        System.out.println(ansUser);
        System.out.println(Thread.currentThread().getName() + "结束");

    }

    static class Thread3 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            int i = 0;
            System.out.println("值为：" + i);
            for (int j = 0; j < 10; j++) {
                i++;
            }
            System.out.println("值为；" + i);
            return i;
        }
    }

    static class Thread2 implements Runnable {
        @Override
        public void run() {
//            int i = 0;
//            System.out.println("值为：" + i);
//            for (int j = 0; j < 10; j++) {
//                i++;
//            }
//            System.out.println("值为；" + i);
            User user = new User();
            user.setAge(21);
            user.setName("李佳乐在阿里巴巴");
        }
    }

    static class Thread1 extends Thread {
        @Override
        public void run() {
            int i = 0;
            System.out.println("值为：" + i);
            for (int j = 0; j < 10; j++) {
                i++;
            }
            System.out.println("值为；" + i);
        }
    }
}
