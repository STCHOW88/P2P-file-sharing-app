/*
This class is used for creating instances of Bitfield Messages
With this class, we create instances for BITFIELD Message types.
*/
public class Type1Message extends Message{
    
    private byte[] messagePayload;

    public Type1Message(){
        this.messageLength=0;
        this.messageType=-1;
    }

    public Type1Message(int messageLength, byte messageType, byte[] messagePayload){
        this.messageLength=messageLength;
        this.messageType=messageType;
        this.messagePayload=messagePayload;
    }

    public Type1Message(Object o){
        Type1Message temp=(Type1Message)o;
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
    public void setMessagePayload(byte[] messagePayload)
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
    public byte[] getMessagePayload()
    {
        return messagePayload;
    }    
}