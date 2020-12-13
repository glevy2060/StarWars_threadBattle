package bgu.spl.mics.application;

import java.util.concurrent.CountDownLatch;
public class CountDownInit {
    public static class singleton{
        private static CountDownInit instance=new CountDownInit();
    }
    public static CountDownInit getInstance(){
        return singleton.instance;
    }
    private CountDownLatch count;
    private CountDownInit(){
        count=new CountDownLatch(4);
    }
    public void down(){
        count.countDown();
    }
    public void awaitCount(){
        try{
            count.await();
        }catch (InterruptedException e){ }
    }
}
