/*
This class is used for creating instances of Handshake Message.
In this class, the variable handshakeHeader holds header as well as zero bits.
*/

import java.io.Serializable;

public class HandshakeMessage implements Serializable {
    
    private String handshakeHeader;
    private int peerID;

    public HandshakeMessage(int peerID){
        this.handshakeHeader="P2PFILESHARINGPROJ00000";
        this.peerID=peerID;
    }

    public HandshakeMessage(Object m){
        HandshakeMessage temp=(HandshakeMessage)m;
        this.handshakeHeader=temp.getHandshakeHeader();
        this.peerID=temp.getPeerID();
    }

    /*
    Function Description: This function is used to set handshakeHeader variable

    Parameters: HandshakeHeader
    */

    public void setHandshakeHeader(String handshakeHeader){
        this.handshakeHeader=handshakeHeader;
    }

    /*
    Function Description: This function is used to set peerID variable

    Parameters: PeerID
    */

    public void setPeerID(int peerID){
        this.peerID=peerID;    
    }

    /*
    Function Description: This function is used to get handshakeHeader variable
 
    Returns: HandshakeHeader
    */

    public String getHandshakeHeader(){
        return handshakeHeader;
    }

    /*
    Function Description: This function is used to get peerID variable

    Return Type: PeerID
    */

    public int getPeerID(){
        return peerID;
    }

}