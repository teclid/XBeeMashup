/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xbeemashup;
import java.util.concurrent.Semaphore;
import java.io.*;
import java.io.PrintWriter;
import java.net.Socket;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.AtCommand;
import com.rapplogic.xbee.api.zigbee.NodeDiscover;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.AtCommandResponse;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;


/**
 *
 * @author jescarri_mx
 */
public class nodeDiscover extends Thread {
        private Semaphore readOk;
        private XBee xbee;
        private long timeOut;
        private ArrayList nodes = new ArrayList();
        private long sleepND;
        csma_ca locking;
public nodeDiscover( Semaphore lock, XBee xbee, long timeOut, long sleepND ){
    this.readOk = lock;
    this.xbee = xbee;
    this.timeOut = timeOut;
    locking = new csma_ca(readOk);
    this.sleepND = sleepND;
}

@Override
public void run(){
    tcpServer readServer = new tcpServer(7777);
    int res = readServer.bindServer();
    while (true){    
        PrintWriter out = null;
        Socket clientSocket = null;
        clientSocket = readServer.startListening();
        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }catch(Exception e){
            out.println(e.toString());
            try{
                clientSocket.close();
            }catch(Exception e1){
                e.printStackTrace();
            }

        }
        if(locking.getLock()){
            try {
                xbee.sendAsynchronous(new AtCommand("ND"));
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < timeOut ){
                XBeeResponse response = (XBeeResponse) xbee.getResponse(1000000);                
                if(response.getApiId() == ApiId.AT_RESPONSE){
                        NodeDiscover node = NodeDiscover.parse((AtCommandResponse)response);
                        out.println(node.toString());
                    }
                }

               locking.releaseLock();
               clientSocket.close();
            }catch(Exception e){
                locking.releaseLock();
                e.printStackTrace();
            }
        }


    }
}

}
