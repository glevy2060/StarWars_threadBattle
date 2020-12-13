package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CountDownInit;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        //subscribe BombEvent
        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent b)->{
            try {
                Thread.sleep(duration);
                sendBroadcast(new TerminationBroadcast());
                complete(b,true);
            } catch (InterruptedException e) {}
        });

        //subscribe termination BroadCast
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast t) ->{
            terminate();
            diary.setLandoTerminate(System.currentTimeMillis());
        });
        CountDownInit.getInstance().down();
    }
}
