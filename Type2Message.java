/*
This class is used for creating instances of certain Messages types
With this class, we create instances for HAVE and REQUEST  Message types.
*/
public class Type2Message extends Message{
    
    private int messagePayload;

    public Type2Message(){
        this.messageLength=0;
        this.messageType=-1;
    }

    public Type2Message(int messageLength, byte messageType, int messagePayload){
        this.messageLength=messageLength;
        this.messageType=messageType;
        this.messagePayload=messagePayload;
    }

    public Type2Message(byte messageType, int messagePayload){
        this.messageType=messageType;
        this.messagePayload=messagePayload;
        this.messageLength=5;
    }

    public Type2Message(Object o){
        Type2Message temp=(Type2Message)o;
        this.messageLength=temp.getMessageLength();
        this.messageType=temp.getMessageType();
        this.messagePayload=temp.getMessagePayload();
    }
    
    /*
    Function Description: This function is used to set messageLength variable

    Parameters: MessageLength
    */
    public void setMessageLength(int messageLength)
    {
        super.setMessageLength(messageLength);
    }

    /*
    Function Description: This function is used to set messageType variable

    Parameters: MessageType
    */
    public void setMessageType(byte messageType)
    {
        super.setMessageType(messageType);    
    }

    /*
    Function Description: This function is used to set messagePayload variable

    Parameters: MessagePayload
    */
    public void setMessagePayload(int messagePayload)
    {
        this.messagePayload=messagePayload;    
    }

    /*
    Function Description: This function is used to get MessageLength variable

    Returns: MessageLength
    */
    public int getMessageLength()
    {
        return super.getMessageLength();
    }

    /*
    Function Description: This function is used to get MessageType variable

    Returns: MessageType
    */
    public byte getMessageType()
    {
        return super.getMessageType();
    }

    /*
    Function Description: This function is used to get MessagePayload variable

    Returns: MessagePayload
    */
    public int getMessagePayload()
    {
        return messagePayload;
    }    
}