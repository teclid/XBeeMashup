/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xbeemashup;
import java.util.concurrent.Semaphore;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeResponse;

/**
 *
 * @author jesus
 */
public class readerCleaner extends Thread {
protected Semaphore readOk;
protected XBee xbee;
 public readerCleaner(Semaphore readOk, XBee Xbee){
        this.readOk = readOk;
        this.xbee = Xbee;
    }
    @Override
    public void run(){
        csma_ca locking = new csma_ca(readOk);
        try{
            while(true){
              if(!Thread.interrupted()){  
                if (locking.getLock()) {
                 try{
                   XBeeResponse response = xbee.getResponse(10);
                   //System.out.println(response.toString());
                   //System.out.println("Estoy Limpiando el Buffer");
                 }catch (Exception e){

                 }
                locking.releaseLock();
                Thread.sleep((int) Math.round(4 * Math.random() - 0.5));
              }
            }
         }

        }catch(InterruptedException ex){
             locking.releaseLock();
             //System.out.println("INTERRUMPIDO");
             Thread.currentThread().interrupt();
             ex.printStackTrace();
        }
       
    }
}
