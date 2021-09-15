package com.duanxu.thread;

/**
 * Description:
 * date: 2021/9/15 20:56
 * Package: com.duanxu.thread
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class Profiler {

    //计算方法耗时的工具类
    private static final ThreadLocal<Long> TIME_THREAD_LOCAL = new ThreadLocal<Long>() {
        //第一次get方法时调用初始化值方法，前提是set方法没有被调用过，每个线程会调用一次
        @Override
        protected Long initialValue() {
            return System.currentTimeMillis();
        }
    };

    public static final void begin() {
        TIME_THREAD_LOCAL.set(System.currentTimeMillis());
    }

    public static final long consum() {
        return System.currentTimeMillis() - TIME_THREAD_LOCAL.get();
    }
}
