/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xbeemashup;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jescarri_mx
 */
public class csma_ca {
    protected Semaphore colisiondetection;
    protected int MAX_RETRANS=10;

    public csma_ca(Semaphore sem){
        this.colisiondetection = sem;
    }
public boolean getLock(){
    boolean adquired = false;
    long csmacadelay=0;
    long backoff=0;

    while (!adquired){
        try{
           adquired = this.colisiondetection.tryAcquire(1,100,TimeUnit.MILLISECONDS);
           if (!adquired){
              csmacadelay = backoff + (long)(1.5 * Math.random() + 0.5);
              //System.out.println(Thread.currentThread().getName()+" CSMACADELAY: "+csmacadelay);
              backoff = (long)((19 * Math.random()+0.5));
              //System.out.println(Thread.currentThread().getName()+"  BACKOFF CALCULATED: "+backoff);
              MAX_RETRANS--;
              //System.out.println(Thread.currentThread().getName()+"---------------- MAX_RETRANS "+MAX_RETRANS);
              Thread.sleep(csmacadelay);
           }else {
              //System.out.println(Thread.currentThread().getName()+"  LOCK ADQUIRED READY TO TX/RX"+ System.nanoTime());
              return adquired;
           }
           if(MAX_RETRANS==0){
               System.out.println(Thread.currentThread().getName()+"  MAX RETRANS REACHED MEDIA NOT ACCESSIBLE");
               break;
           }
           
        }catch(Exception e){

        }
    }
    return adquired;
}

public void releaseLock(){
   this.colisiondetection.release();
 }
}
