package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

public class AttackEvent implements Event<Boolean> {
    private int duration;
    private List<Integer> ewoksSerial;

    public AttackEvent(Attack attack){
        this.duration = attack.getDuration();
        this.ewoksSerial = attack.getSerials();
    }

    public int getDuration() {
        return duration;
    }

    public List<Integer> getEwoksSerial() {
        return ewoksSerial;
    }
}
