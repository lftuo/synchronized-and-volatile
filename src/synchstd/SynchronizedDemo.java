package synchstd;

/**
 * 不加synchronized情况下，线程有可能交叉执行，read和write的执行顺序及结果有可能是：
 *  1.1->1.2->2.1->2.2  =>6
 *  1.1->2.1->1.2->2.2  =>6
 *  1.1->2.1->2.2->1.2  =>3
 *  1.2->2.1->2.2->1.1  =>0(重排序)
 *  ...
 * 加入synchronized关键字，相当于加了一把锁，避免了线程在锁内部交叉执行，线程安全。
 * 写操作先获取write同步语句块，则获得了SynchronizedDemo对象的锁，在写操作释放之前，读操作线程对SynchronizedDemo对象所有同步语句块的访问都会被阻塞。
 */
public class SynchronizedDemo {

    // 共享变量
    private boolean ready = false;
    private int result = 0;
    private int number = 1;

    // 写操作
    public synchronized void write() {
        ready = true;   // 1.1
        number = 2;     // 1.2
    }

    // 读操作
    public synchronized void read(){
        if (ready){     // 2.1
            result = number * 3;    // 2.2
        }

        System.out.println("result的值为："+result);
    }

    // 内部线程类
    private class ReadWriteThread extends Thread{

        // 根据构造器方法中传入的flag参数，确定线程执行读操作还是写操作
        private boolean flag;
        public ReadWriteThread(boolean flag){
            this.flag = flag;
        }

        @Override
        public void run() {

            if (flag){
                // 构造方法中传入true，则执行写操作
                write();
            } else {
                // 构造方法中传入false，则执行读操作
                read();
            }
        }
    }

    public static void main(String[] args){

        SynchronizedDemo demo = new SynchronizedDemo();

        // 启动线程执行写操作
        demo.new ReadWriteThread(true).start();
        // 启动线程执行读操作
        demo.new ReadWriteThread(false).start();

    }
}
