package xbeemashup;
import java.util.concurrent.Semaphore;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jescarri_mx
 */
public class threadTest extends Thread {
   protected Semaphore csma;
   protected csma_ca locking;
   public threadTest (Semaphore Lock){
       this.csma = Lock;
       locking = new csma_ca(Lock);
   }

   public void run(){
       while (true){
           String threadName=Thread.currentThread().getName();
           
           try{
            locking.getLock();
            ////System.out.println("ADQUIRINGLOCK... "+threadName);
            Thread.sleep((long) Math.random()*2);
            //System.out.println("RELEASING LOCK" + threadName);
            locking.releaseLock();
            Thread.sleep((long) Math.random()*2);
           }catch(Exception e){
               e.printStackTrace();
           }

       }
   }
}
