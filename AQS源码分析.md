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

