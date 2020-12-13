package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CountDownInit;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.AttackResolved;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private Ewoks ewoks;
    public C3POMicroservice() {
        super("C3PO");
        ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        // subscribe AttackEvent;
        subscribeEvent(AttackEvent.class, (AttackEvent a)-> {
            ewoks.acquire(a.getEwoksSerial());
            try {
                Thread.sleep(a.getDuration());
            } catch (InterruptedException e) {}
            ewoks.release(a.getEwoksSerial());
            sendBroadcast(new AttackResolved());
            complete(a, true);
            diary.setTotalAttacks();
            diary.setC3POFinish(System.currentTimeMillis());
        } );

        //subscribe termination BroadCast
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast t) ->{
            terminate();
            diary.setC3POTerminate(System.currentTimeMillis());
        });
        CountDownInit.getInstance().down();
    }
}
