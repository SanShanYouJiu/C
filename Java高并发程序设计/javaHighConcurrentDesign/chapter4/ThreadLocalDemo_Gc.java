package javaHighConcurrentDesign.chapter4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**在JDK8中取不到GC的相关输出 这是在JDK7中运行测试的代码
 */
public class ThreadLocalDemo_Gc {

 static volatile ThreadLocal<SimpleDateFormat> tl =new ThreadLocal<SimpleDateFormat>(){
     @Override
     protected void finalize() throws Throwable {
         System.out.println(this.toString()+"is gc");
     }
 };

    static volatile CountDownLatch cd = new CountDownLatch(10000);

    public static class  ParserDate implements  Runnable{
       int i=0;

       public ParserDate(int i){
           this.i=i;
       }

        @Override
        public void run() {
            try {
                if (tl.get() == null) {
                    tl.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"){
                        @Override
                        protected void finalize() throws Throwable {
                            System.out.println(this.toString()+"is gc");
                        }
                    });
                    System.out.println(Thread.currentThread().getId() + ":create SimpleDateFormat");
                }
                Date t = tl.get().parse("2015-03-29 19:29:" + i % 60);
            }catch (ParseException e){
                e.printStackTrace();
            }finally {
                cd.countDown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10000; i++) {
            es.execute(new ParserDate(i));
        }
        cd.await();
        System.out.println("mission complete!");
        tl=null;
        System.gc();
        System.out.println("first GC complete!!");
        //在设置ThreadLocal的时候 会清除ThreadLocalMap中的无效对象
        tl =new ThreadLocal<SimpleDateFormat>();
        cd = new CountDownLatch(10000);
        for (int i = 0; i < 10000; i++) {
            es.execute(new ParserDate(i));
        }
        cd.await();
        Thread.sleep(1000);
        System.gc();
        System.out.println("second GC complete!!");

    }
}
