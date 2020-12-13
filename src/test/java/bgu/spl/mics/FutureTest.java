package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {
    private Future<String> future;
    private Future<String> future2;
    private String res;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
        res="hello";
    }

    @Test
    public void testResolve(){
        //arrange
        String str = "someResult";
        //set
        future.resolve(str);
        //assert
        assertTrue(future.isDone());
        assertEquals(str,future.get());
    }

    @Test
    void get() {

        //arrange
        future.resolve(res);
        //set
        String res2=future.get();
        //assert
        assertTrue(future.isDone());
        assertEquals(res,res2,"the results are not equal");
    }

    @Test
    void isDone() {
        assertFalse(future.isDone());
        future.resolve(res);
        assertTrue(future.isDone());
    }

    @Test
    void testGet() {
        try {
            assertNull(future.get(100, TimeUnit.MILLISECONDS));
            Thread.sleep(10);
            future.resolve(res);
            assertEquals(res,future.get(100,TimeUnit.MILLISECONDS));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
