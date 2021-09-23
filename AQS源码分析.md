AQS为构建锁和同步器供了一些通用的功能实现，因此借助于AQS能简单且高效的构造出一些同步器，比如reentrantlock等这种独占式资源获取的同步器，以及Semaphore，countdownlatch等这种共享式的同步器

AQS的核心思想是如果被请求的资源是空闲状态，则将当前的请求线程设置为有效的工作线程，并且将共享资源的状态设置为锁定状态，如果被请求的共享资源被占用，那么就需要使用一套线程阻塞等待以及唤醒时锁分配的一种机制，这个机制就是由AQS的CLH队列来实现的，也就是说将获取不到锁的线程放到一个队列中。这个CLH队列是一个虚拟的双向队列，虚拟的意思是不存在这个队列的实例，仅存在节点之间的相关关系

总的来说AQS使用一个int型的成员变量来表示同步状态，通过内置的fifo队列来完成获取资源线程的排队工作，然后内部使用CAS来对这个同步状态进行更改

AQS定义了两种对资源的获取方式，一种是独占式，一种是共享式

独占式表示共享资源在同一时刻只能由一个线程进行操作，独占式又分为两种：

1.公平锁：按照线程在队列中的排队顺序，先到者先拿到锁

2.非公平锁：当线程想要获取锁的时候，先通过两次cas操作去抢锁，如果没抢到，会将当前线程加入到队列中等待唤醒

总结一下非公平锁和公平锁其实只有两处地方不同：

1.非公平锁在调用lock后，首先就会调用cas进行一次抢锁，如果这个时候恰巧锁没有被占用，那么就直接获取到锁然后返回

2.如果在第一次cas失败后，和公平锁一样会进入到tryacquire方法，在这个方法中，如果发现这个时候state==0了，也就是说锁被释放了，非公平锁又会开始一次cas抢锁，但是公平锁会判断等待队列里面是否有线程正在等待，如果有线程（Node节点），那么公平锁会自己乖乖的到队列末尾排队

注意到非公平锁其实比公平锁多了两次cas操作，但是如果两次cas都失败了，那么后面就跟公平锁一样，都要进入队列进行等待

lock接口的实现类基本都是通过聚合了一个AQS的子类来完成线程的访问控制

通过源码发现，非公平锁一上来调用lock，然后代理到sync的lock，然后这个lock是抽象方法，转到nonfairsync的lock，可以看到非公锁里面一上来就是cas抢占，抢到了就设置当前当前线程为独占资源的线程，，否则进入acquire

~~~java
        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }
~~~

~~~java
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
~~~

这个acquire可以看做是一个模板，因为里面第一个方法tryacquire是一个抽象方法，对于不同的锁又有不同的实现：

~~~java
        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

~~~

上面的第一个代码块里面可以看到，如果第一个线程来了，发现共享资源的状态，也就是state变量为0，那么给它设置为1，然后将当前线程设置为持有锁的线程，这时候后面再来线程，还是一样的逻辑，直接cas，但肯定失败，因为线程1这时候还没有释放锁，那么会进入acquire方法

现在讨论非公平锁状态下线程2进入后最终走到acquire里面，记住这里穿入的参数是1

~~~java
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
~~~

~~~java
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * Performs lock.  Try immediate barge, backing up to normal
         * acquire on failure.
         */
        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }
~~~

这个tryAcquire方法在AQS里直接抛出异常，强制子类对他进行实现，模板方法设计模式，那么可要看到对于非公平锁，

NonfairSync这个子类确实对这个发法进行了实现，调用了nonfairTryAcquire

~~~java
  final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
      		// 这个判断逻辑是判断当前占有锁的线程是不是自己这个线程，如果是的话，那么证明你这现在是重入了一遍锁
            // 那么state 会变为nextc的值，nextc的值是2，表示这个state状态变为了2，那么就是重入了
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
~~~

这个时候线程2就开始执行nonfairTryAcquire方法，来判断state是不是0，如果是0，直接cas，如果不是0，在判断当前线程是不是独占拥有锁的线程，可以肯定的是，如果线程1这时候还没有进行释放的话，那么这个方法会返回false，最终上一个判断会对这个false取反，为true，那就接着往下走

~~~java
    public final void acquire(int arg) {
        // !tryAcquire(arg) 为 true
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
~~~

至此第一个tryAcquire分析完毕，当前是非公平锁模式

那个假设线程2没有抢到锁，从tryAcquire中返回了false，取反为true，那么&&运算逻辑继续往下走，该到了入队操作了addWaiter

~~~java
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        // 目前这个tail为空，所以第一次进来pred为null，直接走enq方法
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }
~~~

这里面一上来创建一个node，把当前线程包裹进去，相当于把当前线程包装为链表这个数据结构的一个节点，进行入队

~~~java
    private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
~~~

这个非常核心，可以看到封装好了线程2的node以后，执行enq，但是刚开始由于tail属性为空，所以compareAndSetHead传进去了一个空的系统自己创建的node作为一个头结点，这个节点并不是我们的抢占失败的线程节点，而是代码new出来的一个空节点，哑节点

由于每一个node都有一些属性，比如thread，waitstatus等，那么对于这个哑节点来说thread为null，waitstatus为0，基本数据类型作为静态的成员变量，初始值为0

由于是死循环，第二次循环的时候这个tail已经指向了哑节点，所以t不为空，进入第二个代码块，这个代码块的主要作用是将哑节点的next指针指向新加进来的线程2节点，线程2节点的prev指针指向哑节点，并且tail指针指向新加进来的线程2节点，这时候相当于把第一个进来抢占的线程放到了队列中

~~~java
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }
~~~

假设这时候来了线程3，前面逻辑不用多说，进入addwaiter，发现这时候这个tail指针指向就不为空了，因为这个时候tail指针指向了线程2这个node节点，但是一定要记住线程2前面一直会有一个哑节点，那么这时候pred节点，也就是tail指针指向的节点，不为空了，那么不会进入enq这个方法，直接在addwaiter里面做设置尾节点的操作，并且让最后一个节点与倒数第二个节点之间建立起联系，也就是next指针和prev指针。

#### 分析到目前为止，线程1独占着锁，队列里没有它，线程2跟在队列中的哑节点后面，线程3跟在线程2后面

接下来该分析最后一个重要的方法acquireQueued(addWaiter(Node.EXCLUSIVE), arg))，addwaiter返回值是一个node，作为参数传递给acquireQueued

~~~java
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
~~~

final Node p = node.predecessor();这个方法返回一个队列的第一个节点，也就是哑节点

~~~java
   if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
~~~

这里面第一个判断，哑节点是不是头结点，为true，然后继续抢占，但是假如线程1还没走，线程2还是抢占失败

```java
    if (shouldParkAfterFailedAcquire(p, node) &&
        parkAndCheckInterrupt())
        interrupted = true;
}
```

接下来进入shouldParkAfterFailedAcquire，听名字就知道他是抢占失败以后应该要借助locksupport的支持来对线程进行阻塞

~~~java
 private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
            /*
             * This node has already set status asking a release
             * to signal it, so it can safely park.
             */
            return true;
        if (ws > 0) {
            /*
             * Predecessor was cancelled. Skip over predecessors and
             * indicate retry.
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            /*
             * waitStatus must be 0 or PROPAGATE.  Indicate that we
             * need a signal, but don't park yet.  Caller will need to
             * retry to make sure it cannot acquire before parking.
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
~~~

其实这个方法的目的就是为了改变哑节点的waitstatus的值，从默认的0变为-1，由于这个方法被外面的acquireQueued的死循环进行包裹，所以下次再进来执行的时候发现哑节点的ws变为了-1，那么这个方法返回true，一旦返回true，就会接着往下走parkAndCheckInterrupt，这里面才是真正对线程2进行阻塞

~~~java
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }

~~~

到此线程2的node才会在队列中坐稳，后面的线程3的node也跟线程2的node一样，在等候区坐稳，等待线程1的解锁

接下来分析线程1的unlock方法

