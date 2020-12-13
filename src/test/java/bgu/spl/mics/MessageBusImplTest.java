package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.AttackResolved;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private Future future;
    private MessageBusImpl mb;
    private LeiaMicroservice leia;
    private C3POMicroservice cepo;
    private HanSoloMicroservice hanSolo;
    private TerminationBroadcast b;
    @BeforeEach
    void setUp() {
        future=null;
        mb=MessageBusImpl.getInstance();
        List<Integer> serialNumbers=new ArrayList();
        int dur=7;
        serialNumbers.add(2);
        serialNumbers.add(3);
        Attack[] attacks=new Attack[2];
        attacks[0]=new Attack(serialNumbers,dur);
        leia=new LeiaMicroservice(attacks);
        cepo=new C3POMicroservice();
        hanSolo=new HanSoloMicroservice();
        b=new TerminationBroadcast();
        mb.register(leia);
        mb.register(cepo);
        mb.register(hanSolo);
    }

    @Test
    void complete() {
        //arrange
        DeactivationEvent e=new DeactivationEvent();
        mb.subscribeEvent(DeactivationEvent.class,leia);
        boolean res=true;
        future=leia.sendEvent(e);
        //set
        mb.complete(e, res);
        //assert
        assertTrue(future.isDone());
        assertEquals(true,future.get());
    }

    @Test
    void sendBroadcast() {
        //arrage

        //set
        mb.subscribeBroadcast(TerminationBroadcast.class,leia);
        mb.sendBroadcast(b);
        try {
            Message leiaMessager=mb.awaitMessage(leia);
            assertEquals(leiaMessager,b,"Leia's queue does not have the broadcast");
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Test
    void sendEvent() {
        //arrange
        DeactivationEvent de=new DeactivationEvent();
        mb.subscribeEvent(DeactivationEvent.class,cepo);
        //set
        future=leia.sendEvent(de);
        //assert
        assertNotNull(future);
    }

    //can assume that the queue is not empty
    //getters are not allowed
    @Test
    void awaitMessage() {

        //arrange
        mb.subscribeBroadcast(TerminationBroadcast.class,leia);
        mb.sendBroadcast(b);
        //set
        try {
            Message m=mb.awaitMessage(leia);
            //assert
            assertEquals(m,b);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}