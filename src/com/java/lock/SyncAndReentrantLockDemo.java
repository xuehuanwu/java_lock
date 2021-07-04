package com.java.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 题目：synchronized和lock有什么区别？用新的lock有什么好处？
 * 1、原始构成
 * synchronized：是关键字，属于jvm层面的锁
 * 经过javac编译后，生成一个monitorenter、两个monitorexit(一个正常退出，一个异常退出)，共三条命令。
 * monitorenter底层是通过monitor对象来完成，其实wait/notify等方法也依赖于monitor对象，只有在同步块或方法中才能调用wait/notify等方法
 * lock：是具体类(java.util.concurrent.locks.lock)，属于api层面的锁
 * 2、使用方法
 * synchronized：不需要用户去手动释放锁，当synchronized代码执行完后，系统会自动让线程释放对锁的占用(报异常也会释放锁)。
 * ReentrantLock：需要用户去手动释放锁，若没有主动释放，就有可能导致出现死锁现象。
 * 需要lock()和unlock()方法配合try/finally语句块来完成
 * 3、等待是否可中断
 * synchronized：不可中断，除非抛出异常或者正常运行完成
 * ReentrantLock：可中断
 * 一种，设置超时方法tryLock(long timeout, TimeUnit unit)
 * 另一种，lockInterruptibly()放代码块中，调用interrupt()方法可中断
 * 4、加锁是否公平
 * synchronized：非公平锁
 * ReentrantLock：两者都可以，默认非公平锁，构造方法可以传入boolean值，true为公平锁，false为非公平锁
 * 5、锁绑定多个条件condition
 * synchronized：没有
 * ReentrantLock：用来实现分组唤醒需要唤醒的线程们，可以精确唤醒，而不是像synchronized要么随机唤醒一个线程要么唤醒全部线程
 * =====================================================================================================================
 * 题目：多线程之间按顺序调用，实现A->B->C三个线程启动，要求如下，
 * AA打印5次，BB打印10次，CC打印15次
 * 来10轮
 */
public class SyncAndReentrantLockDemo {

    public static void main(String[] args) {
        ShareResource shareResource = new ShareResource();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    shareResource.print5();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "AA").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    shareResource.print10();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "BB").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    shareResource.print15();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "CC").start();
    }
}

class ShareResource {

    private int number = 1; //AA:1,BB:2,CC:3
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    public void print5() throws Exception {
        lock.lock();
        try {
            // 1、判断
            while (number != 1) {
                c1.await();
            }
            // 1、干活
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            // 3、通知
            number = 2;
            c2.signal();
        } finally {
            lock.unlock();
        }
    }

    public void print10() throws Exception {
        lock.lock();
        try {
            // 1、判断
            while (number != 2) {
                c2.await();
            }
            // 1、干活
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            // 3、通知
            number = 3;
            c3.signal();
        } finally {
            lock.unlock();
        }
    }

    public void print15() throws Exception {
        lock.lock();
        try {
            // 1、判断
            while (number != 3) {
                c3.await();
            }
            // 1、干活
            for (int i = 1; i <= 15; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            // 3、通知
            number = 1;
            c1.signal();
        } finally {
            lock.unlock();
        }
    }

}