package com.duanxu.vm;

import java.util.ArrayList;
import java.util.Date;
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
public class OOMTest1 {

    /**
     * 内存溢出：
     * 通俗理解就是内存不够了
     * 内存泄露：
     * 是指程序在申请内存后，无法释放已经申请的内存空间，即这个对象已经不再被使用了，但是GCroots还能有到这个对象的可达的引用链
     * 由于是跟引用相关，由于引用一直得不到释放，导致内存泄露，所以内存泄露只跟强引用有关，其他引用会被GC清掉
     */
    static class OOMObject {

    }

    /*
      java的oom异常大致可以分为以下几种；
      1.堆溢出 java heap space
      2.永久代溢出 permgen space
      3.不能创建线程 unable to create new native thread
     */
    public static void main(String[] args) throws InterruptedException {
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
          若不是内存泄露，即内存中的对象确实都必须存活，则应：
          1.检查JVM参数，与机器的内存做一个对比，看是否还有向上调整的空间
          2.检查代码中是否存在某些对象的生命周期过长，持有状态时间长，存储结构设计不合理等情况，尽量减少程序运行期的内存消耗
         */
        List<OOMObject> list = new ArrayList<>();
        while (true) {
            list.add(new OOMObject());
        }
    }
}
