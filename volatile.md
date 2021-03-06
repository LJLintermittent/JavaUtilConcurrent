在多线程并发编程中，volatile是一种轻量级的synchronized，它在多处理的开发中保证了共享变量的可见性。可见性是当一个线程修改了某个共享变量的值，那么另一个线程总是能读到最新的值。

通过获取JIT编译器生成的汇编指令，查看volatile在并发写操作的时候发生了什么

可以了解到有一个lock前缀的指令，这个指令在多核处理器下会发生两件事情：

1.将当前处理器的缓存行的数据写回到系统内存

2.这个写回内存的操作会使其他cpu里缓存了该内存地址的数据无效

因为，为了提高处理速度，处理器不直接与内存打交道，而是先将系统内存的数据读取到cpu的高速缓存后再进行操作。但操作完不知道何时写回到内存中，如果对声明了volatile的变量进行写操作，jvm就会想处理器发送一条lock前缀的指令，将这个变量所在缓存行中的数据写回到系统内存，但是写回到系统内存，其他处理器的缓存行中的数据依旧是旧数据，所以，为了保证多核处理器各个缓存的一致性，推出了缓存一致性协议，当处理器发现自己缓存行中的数据的内存地址被修改了，就会将当前缓存行设置为无效状态，那么当处理器对这个数据进行修改操作的时候，就会从系统内存中读取最新的值到缓存行中

#### volatile的写读内存语义

当读一个volatile变量时，JMM会把该线程对应的工作内存中的数据置为无效状态，所以线程接下来需要去主内存读取共享变量

当写一个volatile变量时，JMM会把当前线程工作内存中的数据刷新到主内存中

为了实现volatile的这种内存语义，编译器需要在生成字节码的时候，在指令序列中插入内存屏障来限制特定类型的处理器重排序，对于编译器来说，他很难发现一个最优的，也就是最少数量的插入屏障的总数，为此，jmm采取保守策略：
1.在每个volatile写操作前面插入一个storestore屏障

2.在每个volatile写操作后面插入一个storeload屏障

3.每个volatile读前面插入一个loadload屏障

4.每个volatile读后面插入一个loadstore屏障

#### reentrantlock公平锁与非公平锁

公平锁与非公平锁在释放锁的时候都要写一个volatile变量state

公平锁获取的时候，首先去读volatile变量

非公平锁获取的时候，利用CAS更新volatile变量，CAS操作同时具有volatile读和volatile写的内存语义，因为CAS的源码中的cmpxchg会根据处理的核数来决定加不加lock前缀的指令，这个指令会禁止之前和之后的读和写的指令重排序，然后会把工作内存中的数据都写到主内存，并且使其他线程工作内存中的数据为无效

#### 等待通知机制

等待通知的相关方法是任意java对象都具有的，因为这些方法被定义在所有对象的超类中：

notify：通知一个在对象上等待的线程，使其从wait状态返回，而返回的前提的该线程获取到了对象的锁

notifyall：通知所有等待在该对象上的线程

wait：调用该方法的线程进入waiting状态，只有等待另外的线程的通知或被中断才会返回，需要注意的是，调用wait方法的线程会释放锁

wait（long）：超时等待一段时间，参数是毫秒，如果没有通知就超时返回

wait（long，int）：对于超时时间更细的粒度，单位可以达到纳秒

这几个方法的使用细节：

1.使用wait，notify，notifyall方法时需要先对调用对象加锁

2.使用wait方法后，线程会由running变为wait，并将当前线程放到等待队列中

3.notify和notifyall方法调用后，等待线程依旧不会从wait中返回，需要调用notify的线程将锁释放了以后，等待队列中的线程才有机会获取到锁，也就是从wait方法中返回，因为默认是非公平锁，等待队列中的每一个线程都有机会获得锁，没有抢到锁的线程即使被唤醒了也暂时不能从wait中退出

4.notify将等待队列中的一个线程从等待队列移到同步队列，而notifyall则是将等待队列中的所有线程移到同步队列

也就是从waitset转移到了EntryList，EntryList中的线程都是阻塞状态

5.从wait方法返回的前提是获得了对象的锁（只有没获得锁，即使被notify了，也还是block状态）

等待同步机制的目的就是确保等待线程从wait方法返回时能够感知到通知线程对变量所做的修改

​		