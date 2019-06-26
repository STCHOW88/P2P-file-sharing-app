import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
This class is used for doing all work related to logging mechanism.
This class creates a log file for each client and writes log messages into that file based on
the messages received by the peer.
*/
public class Log {

    private File logFile;
    private String currentDir;
    private String fileName;
    private int peerID;

    public Log() {

    }

    public Log(int peerID) {
        this.peerID = peerID;
        currentDir = System.getProperty("user.dir");
        fileName = currentDir+"/log_peer_"+peerID+".log";
        logFile = new File(fileName);
        try {
            if(logFile.exists()){
                PrintWriter tempWriter = new PrintWriter(logFile);
                tempWriter.print("");
                tempWriter.close();
            }else{
                boolean temp = logFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Failure to create Log File");
        }
    }

    /*
    Function Description: This method writes log messages into log file. It is an overloaded method
    The below method is used when a peer finishes downloading the complete file.
    */
    public void writeToLogFile(){
        FileWriter tempFW=null;
        try {
            tempFW = new FileWriter(fileName,true);
        } catch (IOException e) {
            System.out.println("Unable to create FileWriter Object");
        }

        SimpleDateFormat myFormat=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");  
        Date myDate=new Date();  
        String temp=myFormat.format(myDate)+": Peer " + peerID + " has downloaded the complete file.\n";
        try {
            tempFW.write(temp);
        } catch (IOException e) {
            System.out.println("Unable to write to Log File");
        }
        try {
            tempFW.close();
        } catch (IOException e) {
            System.out.println("Unable to close FileWriter Object");
        }
    }

    /*
    Function Description: This method writes log messages into log file. It is an overloaded method
    
    The below method is used for the following cases
    
    1. When a peer makes a connection to another peer.
    2. When a peer receives connection request from another peer and accepts it.
    3. When a peer is unchoked by another peer.
    4. When a peer is choked by another peer.
    5. When a peer has new optimistically unchoked neighbour.
    6. When a peer receives interested message from another peer.
    */
    
    public void writeToLogFile(int logType, int tempPeerID){
        FileWriter tempFW=null;
        try {
            tempFW = new FileWriter(fileName,true);
        } catch (IOException e) {
            System.out.println("Unable to create FileWriter Object");
        }

        SimpleDateFormat myFormat=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");  
        Date myDate=new Date();  
        
        String temp="";
        if(logType==0){
            temp=myFormat.format(myDate)+": Peer "+ peerID + " makes a connection to Peer "+tempPeerID+".\n";
        }

        else if(logType==1){
            temp=myFormat.format(myDate)+": Peer "+ peerID + " is connected from Peer "+tempPeerID+".\n";
        }

        else if(logType==2){
            temp=myFormat.format(myDate)+": Peer "+ peerID +" has the optimistically unchoked neighbour "+tempPeerID+".\n";
        }

        else if(logType==3){
            temp=myFormat.format(myDate)+": Peer "+ peerID+" is unchoked by Peer "+tempPeerID+".\n";
        }

        else if(logType==4){
            temp=myFormat.format(myDate)+": Peer "+ peerID+" is choked by Peer "+tempPeerID+".\n";
        }

        else if(logType==5){
            temp=myFormat.format(myDate)+": Peer "+ peerID+" received the interested message from Peer "+
            tempPeerID+".\n";
        }

        else if(logType==6){
            temp=myFormat.format(myDate)+": Peer "+ peerID+" received the not interested message from Peer "+
            tempPeerID+".\n";
        }

        try {
            tempFW.write(temp);
        } catch (IOException e) {
            System.out.println("Unable to write to Log File");
        }

        try {
            tempFW.close();
        } catch (IOException e) {
            System.out.println("Unable to close FileWriter Object");
        }
    }

    /*
    Function Description: This method writes log messages into log file. It is an overloaded method
    The below method is used when a peer receives have message from another peer for a particular piece index.
    */
    
    public void writeToLogFile(int logType, int tempPeerID, int tempPieceIndex){
        FileWriter tempFW=null;
        try {
            tempFW = new FileWriter(fileName,true);
        } catch (IOException e) {
            System.out.println("Unable to create FileWriter Object");
        }

        SimpleDateFormat myFormat=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");  
        Date myDate=new Date();  
        
        String temp="";
        if(logType==0){
            temp=myFormat.format(myDate)+": Peer "+ peerID+" received the have message from Peer "+
            tempPeerID+" for the piece "+tempPieceIndex+"\n";
        }


        try {
            tempFW.write(temp);
        } catch (IOException e) {
            System.out.println("Unable to write to Log File");
        }

        try {
            tempFW.close();
        } catch (IOException e) {
            System.out.println("Unable to close FileWriter Object");
        }
    }

    /*
    Function Description: This method writes log messages into log file. It is an overloaded method
    The below method is used when a peer changes its preferred neighbour.
    */
    public void writeToLogFile(ArrayList<Integer> nList){
        FileWriter tempFW=null;
        try {
            tempFW = new FileWriter(fileName,true);
        } catch (IOException e) {
            System.out.println("Unable to create FileWriter Object");
        }

        SimpleDateFormat myFormat=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");  
        Date myDate=new Date();  
        
        String temp=myFormat.format(myDate)+": Peer "+peerID+" has the preferred neighbours ";

        for(int i=0;i<nList.size();i++){
            if(i==nList.size()-1){
                temp+=nList.get(i)+".";
            }
            else{
                temp+=nList.get(i)+",";
            }
        }
        temp+="\n";
        try {
            tempFW.write(temp);
        } catch (IOException e) {
            System.out.println("Unable to write to Log File");
        }

        try {
            tempFW.close();
        } catch (IOException e) {
            System.out.println("Unable to close FileWriter Object");
        }
    }

    public void writeToLogFile(int logType, int tempPeerID, int tempPieceIndex, int numPieces){
        FileWriter tempFW=null;
        try {
            tempFW = new FileWriter(fileName,true);
        } catch (IOException e) {
            System.out.println("Unable to create FileWriter Object");
        }

        SimpleDateFormat myFormat=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date myDate=new Date();

        String temp="";
        if(logType==0){
            temp=myFormat.format(myDate)+": Peer "+ peerID+" has downloaded the piece "+tempPieceIndex+" from "
                    +tempPeerID+". Now the number of pieces it has is "+numPieces+".\n";
        }


        try {
            tempFW.write(temp);
        } catch (IOException e) {
            System.out.println("Unable to write to Log File");
        }

        try {
            tempFW.close();
        } catch (IOException e) {
            System.out.println("Unable to close FileWriter Object");
        }
    }

}