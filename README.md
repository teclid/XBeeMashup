XBeeMashup
==========

XBeeMashup


How to Get xBeeMashup working:
1) Clone Git Repo
    git clone https://github.com/teclid/XBeeMashup
2) Open Netbeans and load the Project
3) Fix Dependencies errors with the libraries contained within libraries directory
4) Build the source code
5) On dist folder there will be a jar file execute it

   java -jar jarfile.jar XBEE-EXPLORER-DEVICE NUMBER OF WRITERS
   Replace XBEE-EXPLORER-DEVICE with the serial port where your xBee Coordinator is hooked.
   Example: java -jar "/home/myuser/xbeeMashup/dist/xbeeMashup.jar /dev/ttyUSB0 3

7) Connect to localhost port 9999 to start "Listening" The Traffic from the WSN.

If you have any doubt please contact jesus.carrillo < at> teclid.org or juan.vaca < at> teclid.org

