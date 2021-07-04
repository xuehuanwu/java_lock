package com.java.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 并发包中ReentrantLock的创建可以指定构造函数的boolean类型来得到公平锁或非公平锁，默认是非公平锁
 * =====================================================================================================================
 * 公平锁：Threads acquire a fair lock in the order in which they requested it
 * 公平锁，就是很公平，在并发环境及中，每个线程在获取锁时会先查看此锁维护的等待队列，如果为空，或者当前线程是等待队列的第一个，就占有锁，
 * 否则就会加入到等待队列中，以后会按照FIFO(先进先出)的规则从队列中取到自己
 * =====================================================================================================================
 * 非公平锁：Threads requesting a lock can jump ahead of the queue of waiting threads if the lock happens to be available when it is requested
 * 非公平锁比较粗鲁，上来就直接尝试占有锁，如果尝试失败，就再采用类似公平锁那种方式
 * =====================================================================================================================
 * 非公平锁的优点在于吞吐量比公平锁大
 * 对于sync而言，也是一种非公平锁
 * =====================================================================================================================
 * 可重入锁(也叫做递归锁)：作用是避免死锁
 * ReentrantLock、synchronized都是典型的可重入锁
 * 指同一线程外层函数获得锁之后，内层递归函数仍然能获取该锁的代码，同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁
 * 也就是说，线程可以进入任何一个它已经拥有的锁所同步着的代码块。
 */
public class ReentrantLockDemo {

    public static void main(String[] args) {
        RunLock runLock = new RunLock();
        Thread t3 = new Thread(runLock, "t3");
        Thread t4 = new Thread(runLock, "t4");
        t3.start();
        t4.start();
    }
}

class RunLock implements Runnable {

    /**
     * 公平锁：多个线程按照申请锁的顺序来获取锁，类似排队打饭，先来后到原则。
     */
    Lock tureLock = new ReentrantLock(true);
    /**
     * 非公平锁：多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁。
     * 在高并发的情况下，有可能会造成优先级反转或者饥饿现象。
     */
    Lock falseLock = new ReentrantLock();

    @Override
    public void run() {
        get();
    }

    /**
     * 面试题：同时加两把锁，编译会报错吗？运行会报错吗？
     * 同时加两把锁，编译和运行都不会报错
     * 注意：加锁和解锁必须配对，如果加锁多了，则程序一直运行，不会退出；如果解锁多了，则程序报错java.lang.IllegalMonitorStateException(违法的监控状态异常)
     */
    public void get() {
        falseLock.lock();
//        falseLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t invoken get()");
            set();
        } finally {
            falseLock.unlock();
//            falseLock.unlock();
        }
    }

    public void set() {
        falseLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t invoken set()");
        } finally {
            falseLock.unlock();
        }
    }
}