package com.java.lock;

/**
 * synchronized是非公平锁，也是典型的可重入锁，也是独占锁
 */
public class SyncLockDemo {

    public static void main(String[] args) {
        Phone phone = new Phone();

        new Thread(() -> {
            phone.sendSMS();
        }, "t1").start();

        new Thread(() -> {
            phone.sendSMS();
        }, "t2").start();
    }
}

class Phone {
    public synchronized void sendSMS() {
        System.out.println(Thread.currentThread().getName() + "\t invoken sendSMS()");
        sendEmail();
    }

    public synchronized void sendEmail() {
        System.out.println(Thread.currentThread().getName() + "\t invoken sendEmail()");
    }
}