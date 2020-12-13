package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.AttackResolved;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private int amountOfAttacks; 
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		this.amountOfAttacks = attacks.length;
    }

    @Override
    protected void initialize() {
        //subscribe attackResolved
        subscribeBroadcast(AttackResolved.class, (AttackResolved a) ->{
            amountOfAttacks--;
            if(amountOfAttacks == 0)
                sendEvent(new DeactivationEvent());
        });
        //subscribe termination BroadCast
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast t) ->{
            terminate();
            diary.setLeiaTerminate(System.currentTimeMillis());
        });
        //send all leia attacks
        for (Attack a:attacks) {
            sendEvent(new AttackEvent(a));
        }
    }
}
