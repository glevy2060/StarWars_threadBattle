package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok ewok;
    private Ewok ewok1;
    @BeforeEach
    public void setUp(){
        ewok= new Ewok();
        ewok1=new Ewok();
        ewok.available=true;
        ewok1.available=false;
    }

    @Test
    void acquire() {
        //arrange

        //set
        ewok.acquire();
        ewok1.acquire();
        //assert
        assertFalse(ewok.available,"ewok's available status hasn't change from true to false");
        assertFalse(ewok1.available,"ewok1's available status change from false to true althought it shouldnt change");

    }

    @Test
    void release() {
        //arrange

        //set
        ewok.release();
        ewok1.release();
        //assert
        assertTrue(ewok1.available,"ewok1's available status hasn't change from false to true");
        assertTrue(ewok.available,"ewok's available status changed from true to false althought it shouldnt change");
    }
}