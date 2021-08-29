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
          当发生了java heap space表示java堆内存溢出，那么解决思路应该是这样：
          首先根据dump出来的堆转储快照，使用jprofiler进行内存映像分析，第一步首先应该确定导致oom的对象是否有用，
          也就是说应该分清楚到底是内存泄露还是内存溢出
          如果是内存溢出，可查看泄露对象到GCroots的引用链，可以找到泄露对象是通过怎样的引用路径，与哪些GCroots关联
          才导致垃圾回收期无法回收他们，根据泄露对象的内存信息以及他到GCroots引用链的信息，一般可以准确的定位到这些对象创建的位置，
          进而找出产生内存泄露的代码的具体位置

         */
        List<OOMObject> list = new ArrayList<>();
        while (true) {
            list.add(new OOMObject());
        }

    }
}
