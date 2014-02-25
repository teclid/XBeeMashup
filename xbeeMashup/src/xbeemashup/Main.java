/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xbeemashup;
import java.util.concurrent.Semaphore;
import java.lang.Thread;
import java.io.*;
import com.rapplogic.xbee.api.XBee;

/**
 *
 * @author jescarri_mx
 */
public class Main {
protected static Semaphore lock;
protected static XBee xbee;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
      try{
       InputStreamReader isr = new InputStreamReader(System.in);
       BufferedReader br = new BufferedReader(isr);
       lock = new Semaphore(1,true);
       xbee = new XBee();
       xbee.open(args[0],9600);
       int initial_port = 8889;
       int number_writers = 2;
       for (int i=0; i<number_writers; i++){
           (new Thread (new xbeeWrite(lock, br,xbee,initial_port))).start();
           System.out.println("Started xBeeWriter on port "+initial_port);
           initial_port++;
           Thread.sleep(1000);
       }
       (new Thread (new xbeeRead(lock,xbee))).start();
       (new Thread (new nodeDiscover(lock,xbee,30000,5))).start();
    }catch(Exception e){
            e.printStackTrace();
        }
    }

}
