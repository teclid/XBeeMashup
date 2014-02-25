/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xbeemashup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author jesus
 */
public class tcpServer {
    private int PortNumber;
    public static String BindAddress;
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    tcpServer (int PortNumber){
        this.PortNumber = PortNumber;
       // this.BindAddress = BindAddress;
    }
    public int bindServer(){
        try {
            serverSocket = new ServerSocket(PortNumber);
            return 1;
        }catch(IOException e){
            System.out.println("No pude Bindear el Socket!!!!!");
            e.printStackTrace();
            return 0;
        }
    }
    public Socket startListening(){
        Socket socket = null;
        try{
             clientSocket =  serverSocket.accept();
             return clientSocket;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
       
}
