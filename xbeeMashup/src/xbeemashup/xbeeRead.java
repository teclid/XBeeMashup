/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xbeemashup;
import java.util.concurrent.Semaphore;
import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;
import com.rapplogic.xbee.api.RemoteAtResponse;
import com.rapplogic.xbee.api.AtCommandResponse;
import java.io.PrintWriter;
import java.net.Socket;
/**
 *
 * @author jescarri_mx
 */
public class xbeeRead extends Thread {
protected Semaphore readOk;
protected XBee xbee;

public xbeeRead(Semaphore readOk, XBee Xbee){
    this.readOk = readOk;
    this.xbee = Xbee;
   }

@Override
public void run(){
    Thread cleaner;
    cleaner = new readerCleaner(readOk, xbee);
    cleaner.start();
    tcpServer readServer = new tcpServer(9999);
    int res = readServer.bindServer();
    csma_ca locking = new csma_ca(readOk);
    while(true){
        PrintWriter out = null;
        if(cleaner.isInterrupted()){
            System.out.println("JOINING THREAD!!");
            try{
              cleaner.join();
            }catch (Exception e){}
        }
        if(!cleaner.isAlive()){
                    System.out.println("THREAD DE LIMPIEZA ESTA MUERTO REINICIANDOLO!");
                    cleaner = new readerCleaner(readOk,xbee);
                    cleaner.start();
        }
                Socket clientSocket = null;
        clientSocket = readServer.startListening();
        cleaner.interrupt();
        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }catch(Exception e){
            e.printStackTrace();
        }
       while(clientSocket.isConnected()){
        try{
            if (locking.getLock()) {
                XBeeResponse response = xbee.getResponse(100);
                locking.releaseLock();
                 if ((!response.isError()) && (response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE)) {
                    ZNetRxIoSampleResponse ioSample = (ZNetRxIoSampleResponse) response;
                    out.println("ZNET_IO_SAMPLE_RESPONSE: "+response.toString());
                }
                if ((!response.isError()) && (response.getApiId() == ApiId.ZNET_RX_RESPONSE)) {
                    ZNetRxResponse rxResponse = (ZNetRxResponse) response;
                    out.println("ZNET_RX_RESPONSE: "+response.toString());
                }

                if(out.checkError()){
                        out.close();
                        clientSocket.close();
                       break;
                    }
                    //sleep((int) Math.round(4 * Math.random() - 0.5));
            } //IF DEL LOCK
        }catch(Exception E){
            locking.releaseLock();
         }
      }//WHILE DEL SOCKET
      //System.out.println("SOCKET LECTURA CERRADO");
      System.gc();
    }


    }

}

