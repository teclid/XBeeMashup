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

/**
 *
 * @author jescarri_mx
 * this class implements the request parser and executes the zigbee request
 * creates the network server for writing to the zigbee network.
 */
public class xbeeWrite extends Thread{
protected Semaphore readOk;
protected BufferedReader br;
protected int port;
protected XBee xbee;
public xbeeWrite(Semaphore readOk, BufferedReader br, XBee Xbee, int port){
        this.readOk = readOk;
        this.br = br;
        this.xbee = Xbee;
        this.port = port;
    }
public void run() {
        tcpServer server = new tcpServer(port);
        int res = server.bindServer();
        String inputLine, outputLine;
        PrintWriter out = null;
        BufferedReader in = null;
        Socket clientSocket = null;
        csma_ca locking = new csma_ca(readOk);
        while (true) {
            try {
                try {
                    clientSocket = server.startListening();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                out.println("WELLCOME TO XBEE MASHUP>");
                try {
                    while ((inputLine = in.readLine()) != null) {
                            String a = inputLine;
                            if(a.equals("BYE")){
                                out.close();
                                clientSocket.close();
                                //System.out.println("Me Dijeron Adios");
                                break;
                            }
                            out.println("");
                            
                            if(out.checkError()){
                                //System.out.println("DETECTE LA MUERTE DE LA CONEXION!!!!");
                                out.close();
                                clientSocket.close();
                                break;
                            }
                            boolean adquired = false;
                            out.print("TRIYING TO ADQUIRE LOCK: ");
                            while (!adquired) {
                                adquired = locking.getLock();
                                if (adquired) {
                                    out.println("OK");
                                    try{
                                    requestParser request = new requestParser(a,xbee);
                                    if (request.parseRequest()) {
                                        String respuesta = request.executeRequest();
                                        out.println("RESPUESTA: "+respuesta);
                                        
                                        locking.releaseLock();
                                        out.println("LOCK RELEASED!!!");
                                        System.gc();
                                    } else {
                                        out.println("BADREQUEST");
                                        locking.releaseLock();
                                        out.println("LOCK RELEASED!!!");
                                    }//else   
                                    }catch(Exception e){
                                      locking.releaseLock();
                                    }
                                                                        
                                } 
                              out.println("");
                              if(out.checkError()){
                                 //System.out.println("DETECTE LA MUERTE DE LA CONEXION!!!!");
                                 out.close();
                                 clientSocket.close();
                                 System.gc();
                                 break;
                                }
                            }//While ReadLine
                        }
                    
                } catch (IOException e) {
                    clientSocket.close();
                    locking.releaseLock();
                    //System.out.println("Closed Socket");
                    System.gc();
                }

            } catch (Exception E) {
                this.readOk.release();
                System.gc();
            }
        }
    }
}
