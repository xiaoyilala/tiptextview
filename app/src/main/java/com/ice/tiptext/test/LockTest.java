package com.ice.tiptext.test;
/**
 * synchronized (this){}和其修饰非静态方法 获取的是对象锁；修饰静态方法获取的是Class对象锁
 * thread1.join 保证thread1执行完后再执行之后的方法
 * yield 暂停正在执行的线程对象，不释放锁
 * sleep 线程休眠 但不释放锁
 * wait 线程休眠 释放锁，只能在获取锁后使用（同步块中）
 * await condition中的方法 线程休眠 释放锁，signal 可指定唤醒线程
 *
 * volatile修饰的变量 比如 i = 5 可保证线程安全；i++或者i=i+1不能保证线程安全
 * */
public class LockTest {

    static class SleepTest{

        public static void main(String[] args) {

            SleepTest sleepTest = new SleepTest();
            for(int i=0; i<5; i++){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        sleepTest.m1();
                        m1();
                    }
                }).start();
            }
        }

        static synchronized void m1(){
            String name = Thread.currentThread().getName();
//            synchronized (this){
                try {
                    System.out.println(name+"获取了锁");
                    Thread.sleep(2000);
                    System.out.println(name+"醒来了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }
    }
}
