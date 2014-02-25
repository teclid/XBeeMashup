/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xbeemashup;
import java.util.concurrent.Semaphore;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeRequest;
import com.rapplogic.xbee.api.RemoteAtRequest;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.zigbee.ZNetTxStatusResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.XBeeException;




/**
 *
 * @author jesus
 * Definicion de la Trama
 * TASKID:REMOTEADDR:TYPE:PAYLOAD
 * TASKID: Hash que identifica ese task
 * REMOTEADDR: MAC Address de el modulo zigbee
 * TYPE: Tipo de Request puede ser AT o API
 * PAYLOAD: Datos a enviar, en caso de ser AT el payload contendra comando,dato
 */
public class requestParser{
    /* */
    private String TASKID = null;
    private String REMOTEADDR="";
    //Typo de Request 1 es AT 2 es Api
    private int TYPE = 0;
    private String PAYLOAD = null;
    private String PAYLOAD_AT_COMMAND = null;
    private String DATA_TO_SEND=null;
    private String RawRequest = null;
    private Semaphore Lock;
    private XBee xbee;
    requestParser(String RawRequest,XBee Xbee){
        this.RawRequest = RawRequest;
        this.xbee = Xbee;
    }
   public boolean parseRequest(){
       try{
       //System.out.println("PAREASNDO EL REQUEST: "+ RawRequest);
       String [] tokenizedRequest = RawRequest.split(":");
       for(int i=0;i<tokenizedRequest.length;i++)
          {
             tokenizedRequest[i].trim(); 
          }
       if(tokenizedRequest.length == 4){
       TASKID = tokenizedRequest[0];
       String rawADDr = tokenizedRequest[1];
       String exADDr[] = rawADDr.split(" ");
       String addToConstruct = "";
       String realAddress = "";
       for(int i=0;i<exADDr.length;i++){
          String token[] = exADDr[i].split("x"); 
         REMOTEADDR += token[1]+" ";
       }
       //System.out.print(REMOTEADDR);
       if(tokenizedRequest[2].equals("AT")){
           TYPE = 1;
       }
       if(tokenizedRequest[2].equals("API")){
           TYPE = 2;
       }
       if(TYPE == 0){
           return false;
       }
       /*Parseamos el Payload*/
       PAYLOAD = tokenizedRequest[3];
       //System.out.println("PAYLOAD "+PAYLOAD);
       if(TYPE == 1){
           try{
              
           String [] temporal = PAYLOAD.split(",");
           if(temporal.length == 2 ){
               PAYLOAD_AT_COMMAND = temporal[0];
               //System.out.println("DATA_TO_SEND "+temporal[1]);
               DATA_TO_SEND = temporal[1].trim();
               
           }else{
              return false;
              }
           }catch(Exception e){ 
               e.printStackTrace(); 
               return false;
           
              }   
           
           

       }
       return true;
       }else{
           return false;
       }
       }catch(Exception e){
           e.printStackTrace();
           return false;
       }
   }
   public String executeRequest(){
       String RETVAL="";
       if(TYPE ==1){
           //System.out.println("TaskID: " + TASKID + "REMOTEADDR: "+REMOTEADDR+" TYPE AT AT_PAYLOAD: " + PAYLOAD_AT_COMMAND+DATA_TO_SEND);
           try{
             RemoteAtRequest request = new RemoteAtRequest( new XBeeAddress64(REMOTEADDR), PAYLOAD_AT_COMMAND,new int[]{Integer.parseInt(DATA_TO_SEND)});
             XBeeResponse response = xbee.sendSynchronous(request,4500);
             //System.out.println(response.toString());
             if((response.getApiId() == ApiId.ZNET_TX_STATUS_RESPONSE)){
             ZNetTxStatusResponse txResponse = (ZNetTxStatusResponse) response;
              if (txResponse.isSuccess()) {
                 RETVAL = TASKID +":OK";
                 //System.out.println(txResponse.toString());
             }else{
                  RETVAL = TASKID +":ERROR";
                 }     
             }  
           }catch (XBeeTimeoutException e){
               RETVAL = TASKID +":EXCEPTION";
               System.out.println("TIMEOUT DEL SEND");
               //e.printStackTrace();
               xbee.clearResponseQueue();
               Lock.release();
               
           }catch (XBeeException e){
               RETVAL = TASKID +":EXCEPTION";
               System.out.println("TIMEOUT DEL SEND");
               //e.printStackTrace();
               xbee.clearResponseQueue();
               Lock.release();
           }
             
           try {
               //Thread.currentThread().sleep((int) Math.round(10 * Math.random() - 0.5));
           } catch (Exception E) {
           }
   
       }else{
           System.out.println("TaskID: "+TASKID+"REMOTEADDR: "+REMOTEADDR+" TYPE AT AT_PAYLOAD: "+PAYLOAD_AT_COMMAND);
           
           Lock.release();
           
       }
       
       return RETVAL;
   }
}
