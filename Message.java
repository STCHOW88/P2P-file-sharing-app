/*
This class is used for creating instances of Messages which don't have a payload.
With this class, we create instances for CHOKE, UNCHOKE, INTERESTED and NOT INTERESTED Message types.
*/

import java.io.Serializable;

public class Message implements Serializable {

    protected int messageLength;
    protected byte messageType;

    public Message(){
        this.messageLength=0;
        this.messageType=-1;
    }

    public Message(Object o){
        Message tempMessage=(Message)o;
        this.messageLength=tempMessage.messageLength;
        this.messageType=tempMessage.messageType;
    }

    public Message(byte messageType){
        this.messageType=messageType;
        this.messageLength=1;
    }
    /*
    Function Description: This function is used to set messageLength variable

    Parameters: MessageLength
    */

    public void setMessageLength(int messageLength){
        this.messageLength=messageLength;
    }

    /*
    Function Description: This function is used to set messageType variable

    Parameters: MessageType
    */

    public void setMessageType(byte messageType){
        this.messageType=messageType;    
    }

    /*
    Function Description: This function is used to get messageLength variable
 
    Returns: MessageLength
    */

    public int getMessageLength(){
        return messageLength;
    }
    
    /*
    Function Description: This function is used to get handshakeHeader variable

    Returns: Message Type
    */

    public byte getMessageType(){
        return messageType;
    }
}