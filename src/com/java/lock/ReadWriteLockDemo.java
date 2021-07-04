package com.java.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 独占锁
 * 指该锁一次只能被一个线程所持有。
 * ReentrantLock、synchronized都是独占锁
 * =====================================================================================================================
 * 共享锁
 * 该锁可被多个线程所持有。
 * =====================================================================================================================
 * ReentrantReadWriteLock其读锁是共享锁，写锁是独占锁。
 * 读锁的共享锁可保证并发读是非常高效的，读写、写读、写写的过程是互斥的。
 */
public class ReadWriteLockDemo {

    public static void main(String[] args) {
        MyCache myCache = new MyCache();

        for (int i = 1; i <= 5; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.put(temp + "", temp + "");
            }, String.valueOf(i)).start();
        }

        for (int i = 1; i <= 5; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.get(temp + "");
            }, String.valueOf(i)).start();
        }
    }
}

class MyCache {

    // volatile保证可见性
    private volatile Map<String, Object> map = new HashMap<>();
    // 读写锁同体
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    // 写操作：原子+独占
    public void put(String key, Object value) {
        rwLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t 正在写入: " + key);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.getStackTrace();
            }
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "\t 写入完成");
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // 读操作：共享
    public void get(String key) {
        rwLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t 正在读取");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.getStackTrace();
            }
            Object result = map.get(key);
            System.out.println(Thread.currentThread().getName() + "\t 读取完成: " + result);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void clearMap() {
        map.clear();
    }
}