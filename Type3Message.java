public class Type3Message extends Message{
    private byte[] messagePayload;
    private int pieceIndex;

    public Type3Message(){
        this.messageLength=0;
        this.messageType=-1;
        this.pieceIndex=-1;
        this.messagePayload=null;
    }


    public Type3Message(byte messageType, int pieceIndex, byte[] messagePayload){
        this.messageType=messageType;
        this.messagePayload=messagePayload;
        this.pieceIndex=pieceIndex;
        this.messageLength=5+messagePayload.length;
    }

    public Type3Message(Object o){
        Type3Message temp=(Type3Message)o;
        this.messageLength=temp.getMessageLength();
        this.messageType=temp.getMessageType();
        this.messagePayload=temp.getMessagePayload();
        this.pieceIndex=temp.getPieceIndex();
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

    public int getPieceIndex(){
        return pieceIndex;
    }

    public void setPieceIndex(int pieceIndex){
        this.pieceIndex=pieceIndex;
    }
}
