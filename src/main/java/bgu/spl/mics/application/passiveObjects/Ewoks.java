package bgu.spl.mics.application.passiveObjects;


import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private List<Ewok> ewokList;
    private int activeAcquire;
    private int activeReleaser;

    private static class SingletonHolder{
        private static Ewoks instance = new Ewoks();
    }
    private Ewoks(){
        ewokList=new ArrayList<>();
        activeAcquire = 0;
        activeReleaser = 0;
    }

    public static Ewoks getInstance(){
        return SingletonHolder.instance;
    }

    public void addEwok(Ewok e){
        if(SingletonHolder.instance!=null)
            ewokList.add(e);
    }

    public void acquire(List<Integer> listOfEwoks){
        beforeAcquire();

        for(int ewokIndex: listOfEwoks){
            ewokList.get(ewokIndex-1).acquire();
        }

        afterAcquire();
    }

    public void release(List<Integer> listOfEwoks){
        beforeRelease();

        for(int ewokIndex: listOfEwoks){
            ewokList.get(ewokIndex-1).release();
        }

        afterRelease();
    }

    private synchronized void beforeAcquire(){
        while (!allowAcquire()){
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        activeAcquire++;
    }

    private synchronized void afterAcquire(){
        activeAcquire--;
        notifyAll();
    }

    private synchronized void beforeRelease(){
        while (!allowRelease()){
            try {
                wait();
            } catch (InterruptedException e) {
                //System.out.println(e.getMessage());
            }

        }
        activeReleaser++;
    }

    private synchronized void afterRelease(){
        activeReleaser--;
        notifyAll();
    }

    private boolean allowAcquire(){
        return activeReleaser == 0;
    }

    private boolean allowRelease(){
        return (activeReleaser==0 && activeAcquire ==0);
    }
}
