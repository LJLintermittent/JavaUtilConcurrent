了解synchronized的原理，我想应该先弄明白对象的内存布局，这对理解synchronized的整体过程会有一个非常重要的帮助

在jdk_8_hotspot虚拟机源码中的markOop.hpp文件中的代码注释片段里，分别描述了32位和64位下mark-word的存储状态

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

java1.6为了减少获得锁和释放锁带来的性能消耗，引入了偏向锁和轻量级锁，所以在jdk1.6以后，锁一共有四种状态，级别从低到高分别是无锁，偏斜锁，轻量级锁和重量级锁，这几个状态会随着竞争情况加剧而逐渐升级，锁可以升级但是不能降级。

1.偏向锁：hotspot作者经过研究发现，大多数情况下，锁不仅不存在多线程竞争，而且总是由同一个线程多次获得，为了让同一个线程获得锁的代价更低，从而引入了偏向锁。当一个线程访问同步代码块并获取锁时，会在对象头和栈帧中的锁记录里面存储锁偏向的线程ID，以后该线程再次进入和退出同步块的时候就不需要通过CAS来进行加锁和释放锁，只需要简单的测试一下对象头中的mark word中的偏向锁的锁偏向线程ID是否是指向该线程的，如果测试成功，表明该线程已经获得了锁，如果测试失败，那么会再测试一下mark word中的偏向锁标识是否为1，（表示当前是偏向锁），如果没有设置，则使用CAS竞争锁，如果设置了，则尝试用CAS将对象头的偏向锁指向当前线程。偏向锁的撤销：偏向锁的撤销是使用了一种等待竞争出现才释放锁的机制，所以当其他线程尝试竞争偏向锁的时候，持有偏向锁的线程才会释放锁，同时，偏向锁的撤销，需要等待全局安全点的到来，全局安全点意味着这个时候没有正在执行的字节码，

偏向锁在java6和7中是默认启用的，但是它在应用程序启动几秒钟后才激活，如有必要可以使用JVM来参数来关闭延迟，并且如果确定应用程序在大多数情况下所有的锁通常是出于竞争状态，可以关闭偏向锁，所有程序默认进入轻量级锁状态。

注意，在jdk15的时候系统已经默认关闭了偏向锁优化，原因：

①：偏向锁导致synchronized的子系统代码复杂度过高，导致难以维护和升级

②：偏向锁带来的加锁的性能优化从整体上来看并没有多少收益，并且锁的撤销成本较大，因为需要等待全局安全点，也就是没有正在执行的字节码，再暂停线程做一个锁撤销。

2.