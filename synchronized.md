了解synchronized的原理，我想应该先弄明白对象的内存布局，这对理解synchronized的整体过程会有一个非常重要的帮助

在jdk_8_hotspot虚拟机源码中的markOop.hpp文件中的代码注释片段里，分别描述了32bit和64bit下mark-word的存储状态

~~~c++
//  64 bits:
//  --------
//  unused:25 hash:31 -->| unused:1   age:4    biased_lock:1 lock:2 (normal object)
//  JavaThread*:54 epoch:2 unused:1   age:4    biased_lock:1 lock:2 (biased object)
//  PromotedObject*:61 --------------------->| promo_bits:3 ----->| (CMS promoted object)
//  size:64 ----------------------------------------------------->| (CMS free block)
//
//  unused:25 hash:31 -->| cms_free:1 age:4    biased_lock:1 lock:2 (COOPs && normal object)
//  JavaThread*:54 epoch:2 cms_free:1 age:4    biased_lock:1 lock:2 (COOPs && biased object)
//  narrowOop:32 unused:24 cms_free:1 unused:4 promo_bits:3 ----->| (COOPs && CMS promoted object)
//  unused:21 size:35 -->| cms_free:1 unused:7 ------------------>| (COOPs && CMS free block)
~~~

在64位虚拟机之下，mark-word是64bit大小的。25 31 1 4 1 2

1bit偏向锁标志，2bit锁标志，4bit分代年龄，31 bithashcode

未使用25bit，哈希值31bit，未使用1bit，分代年龄4bit，偏向锁标识1bit，锁类型2bit

观察monitor字节码，在ObjectMonitor.hpp文件中找到源码描述

~~~c++
  ObjectMonitor() {
    _header       = NULL;
    _count        = 0; 记录个数
    _waiters      = 0, 
    _recursions   = 0; 线程重入次数
    _object       = NULL; 存储monitor对象
    _owner        = NULL;持有这个对象的线程
    _WaitSet      = NULL;处于wait状态的线程，会被加入到waitset
    _WaitSetLock  = 0 ;
    _Responsible  = NULL ;
    _succ         = NULL ;
    _cxq          = NULL ;单向链表
    FreeNext      = NULL ;
    _EntryList    = NULL ;处于block状态的线程，会被加入到该列表
    _SpinFreq     = 0 ;
    _SpinClock    = 0 ;
    OwnerIsThread = 0 ;
    _previous_owner_tid = 0;
  }
~~~

获取到了monitor对象的线程会进入owner区，并且count+1，如果线程调用了wait方法，此时会释放monitor对象，owner为空，count-1，同时该线程进入waitset列表，等待被唤醒

每个java对象头中都包括了monitor对象(存储的是指针的指向)。synchronized也就是通过这种方式获取锁，这也解释了synchronized()括号李放任何对象都能获取锁

同步方法：

ACC_SYNCHRONIZED：同步标识，多线程情况下如果看到有这个标识，就会开始竞争monitor对象。

同步代码块：

- `monitorenter`，在判断拥有同步标识 `ACC_SYNCHRONIZED` 抢先进入此方法的线程会优先拥有 Monitor 的 owner ，此时计数器 +1。
- `monitorexit`，当执行完退出后，计数器 -1，归 0 后被其他进入的线程获得

synchronized也能保证变量的可见性：

1.在线程释放锁之前，必须把共享变量的最新值从工作内存刷新回到主内存中

2.线程加锁前，将清空工作内存中共享变量的值，从而使用共享变量时需要从主内存中读取到最新的值

3.volatile 的可见性都是通过内存屏障（Memnory Barrier）来实现的

4.synchronized 靠操作系统内核互斥锁实现，相当于 JMM 中的 lock、unlock。退出代码块时刷新变量到主内存

`as-if-serial`，保证不管编译器和处理器为了性能优化会如何进行指令重排序，都需要保证单线程下的运行结果的正确性。也就是常说的：如果在本线程内观察，所有的操作都是有序的；如果在一个线程观察另一个线程，所有的操作都是无序的

synchronized如果锁的是普通同步方法，锁是当前实例对象

synchronized如果锁的是静态同步方法，锁是当前类的Class对象

如果是同步代码块，锁是代码块里填入的对象

