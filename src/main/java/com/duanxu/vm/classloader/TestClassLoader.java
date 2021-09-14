package com.duanxu.vm.classloader;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Description:
 * date: 2021/9/1 13:37
 * Package: com.duanxu.vm.classloader
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class TestClassLoader {

    public static void main(String[] args) throws ClassNotFoundException {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threads = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo thread : threads) {
            System.out.println("[" + thread.getThreadId() + "]" + thread.getThreadName());
        }
        MyClassLoader classLoader = new MyClassLoader("D:");
        Class<?> clazz = classLoader.loadClass("test.OOMTest2");
        System.out.println(clazz.getClassLoader().getClass().getName());

    }
}
