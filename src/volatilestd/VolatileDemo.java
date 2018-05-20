package volatilestd;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VolatileDemo {

    // 定义volatile变量
    // private volatile int number = 0;
    private int number = 0;

    private Lock lock = new ReentrantLock();
    public int getNumber() {
        return this.number;
    }

    public void increase(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // volatile不能保证原子性，所以存在值不是500的情况
        // this.number++;
        // 解决方案：
        // 1.加入synchronized关键字，保证原子操作；
        // 2.使用ReentrantLock(java.utils.concurrent.locks)；
        // 3.使用AtomicInteger(java.util.concurrent.atomic)
        // 第一种方式
        /*synchronized (this){
            this.number++;
        }*/
        // 第二种方式，相当于进去synchronized语句块，同样会保证可见性/原子性
        lock.lock();
        try {
            this.number++;
        } finally {
            lock.unlock();
        }
    }


    public static Thread[] findAllThreads() {
        ThreadGroup group =
                Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;

        // 遍历线程组树，获取根线程组
        while ( group != null ) {
            topGroup = group;
            group = group.getParent();
        }
        // 激活的线程数加倍
        int estimatedSize = topGroup.activeCount() * 2;
        Thread[] slackList = new Thread[estimatedSize];
        //获取根线程组的所有线程
        int actualSize = topGroup.enumerate(slackList);
// copy into a list that is the exact size
        Thread[] list = new Thread[actualSize];
        System.arraycopy(slackList, 0, list, 0, actualSize);
        return list;
    }
    public static void main(String[] args) throws InterruptedException {

        final VolatileDemo demo = new VolatileDemo();

        for (int i = 0; i < 500; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    demo.increase();
                }
            }).start();
        }

        // 如果还有子线程在运行，主线程就让出CPU资源
        // 直到所有子线程都运行完了，主线程在继续运行下去
        while (Thread.activeCount() > 2){
            Thread.yield();
        }

        System.out.println("number:" + demo.getNumber());

    }

}
