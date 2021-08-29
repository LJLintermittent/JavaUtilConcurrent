package com.duanxu.vm;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * date: 2021/8/29 22:33
 * Package: com.duanxu.vm
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class OOMTest {

    static class OOMObject {

    }

    /*
      java的oom异常大致可以分为以下几种；
      1.堆溢出 java heap space
      2.永久代溢出 permgen space
      3.不能创建线程 unable to create new native thread
     */
    public static void main(String[] args) {
        /*1.java堆溢出，java堆用于储存对象实例，只要不断地创建对象，并且保证GCroots到对象之间有可达路径来避免GC机制
             来清除这些对象，则随着对象的增加，总容量触及最大堆的容量限制后就会产生堆内存溢出异常
          限制java堆内存的最大空间为20mb，不可扩展
          -Xms5m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
         */
        List<OOMObject> list = new ArrayList<>();
        while (true) {
            list.add(new OOMObject());
        }

    }
}
