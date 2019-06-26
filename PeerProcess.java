import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toMap;

/*
Class Description: This class creates an abstraction for a Peer Process
*/
public class PeerProcess {

    //This variable is used to maintain peerID.
    private int peerID;

    //This variable is used to maintain number of preferred neighbours.
    private int numberOfPreferredNeighbours;

    //This variable is used to maintain unchoking interval for preferred neighbours.
    private int unchokingInterval;

    private int optimisticUnchokingInterval;

    private String fileName;
    private int fileSize;
    private int pieceSize;
    private int noOfPieces;

    //This data structure is used to maintain status of pieces received by a peer.
    private byte[] bitfield;

    //This variable is used to write into log file.
    private Log logFile;

    private int piecesDownloaded = 0;

    private Map<Integer, String[]> peerInfoList;

    private String myHostName;
    private int myPort;

    private String filePath;

    //This variable is used to write pieces received by peer into a file
    RandomAccessFile raf;

    
    private HashMap<Integer, Integer> packetsSent;

    private ArrayList<Integer> connectedPeers;
    private ArrayList<Integer> interestedPeersInMe;
    private ArrayList<Integer> prefNeighbours;

    private HashMap<Integer, Threader> connectionThreads;

    private HashMap<String, String> typeDesc;

    private int optimisticNeighbour;

    public PeerProcess(String peerID) throws IOException {
        connectionThreads = new HashMap<>();
        interestedPeersInMe = new ArrayList<>();
        prefNeighbours = new ArrayList<>();
        connectedPeers = new ArrayList<>();
        optimisticNeighbour = -1;

        this.peerID = Integer.parseInt(peerID);
        peerInfoList = new HashMap<Integer, String[]>();
        initConfig("Common.cfg");
        initFile();
        initPeer("PeerInfo.cfg");
        initLogFile();

        new Listener().start();
        initConnections();

        packetsSent = new HashMap<>();
        for (int p : peerInfoList.keySet()) {
            if (p != Integer.parseInt(peerID)) {
                packetsSent.put(p, 0);
            }
        }

        typeDesc = new HashMap<>();
        typeDesc.put("0", "choke");
        typeDesc.put("1", "unchoke");
        typeDesc.put("2", "interested");
        typeDesc.put("3", "Not Interested");
        typeDesc.put("4", "Have");
        typeDesc.put("5", "Bitfield");
        typeDesc.put("6", "Request");
        typeDesc.put("7", "Peice");

    }

    private void initConnections() throws IOException {
        for (Map.Entry<Integer, String[]> pair : peerInfoList.entrySet()) {
            int id = pair.getKey();
            String[] tVals = pair.getValue();
            if (id == peerID) {
                break;
            }

            Socket requestSocket = new Socket(tVals[1], Integer.parseInt(tVals[2]));
            Threader temp = new Threader(requestSocket, id, true);
            logFile.writeToLogFile(0, id);
            connectionThreads.put(id, temp);
            temp.start();

            System.out.println("connection with peer " + id + " created successfully");
        }

        NeighbourTimer neighbourTimer = new NeighbourTimer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(neighbourTimer, 0, unchokingInterval * 1000);

        OptimisticTimer optimisticTimerTimer = new OptimisticTimer();
        Timer timer2 = new Timer(true);
        timer2.scheduleAtFixedRate(optimisticTimerTimer, 0, optimisticUnchokingInterval * 1000);
    }

    private void initFile() throws IOException {
        filePath = "peer_" + peerID;
        File dir = new File(filePath);
        dir.mkdir();
        raf = new RandomAccessFile(filePath + "/" + fileName, "rw");
        raf.setLength(fileSize);
    }

    private void initLogFile() {
        logFile = new Log(peerID);
    }

    /*
    Function Description: This method reads Common.cfg file and assigns appropriate values to instance variables of PeerProcess Class

    Parameters: FileName
    */
    public void initConfig(String file) {
        ArrayList<String> commonVar = new ArrayList<String>();
        try {

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String myLine = null;
            while ((myLine = bufferedReader.readLine()) != null) {
                String[] myVar = myLine.split(" ");
                commonVar.add(myVar[1]);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found - Common.cfg");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Error reading file");
            System.exit(0);
        } finally {
            numberOfPreferredNeighbours = Integer.parseInt(commonVar.get(0));
            unchokingInterval = Integer.parseInt(commonVar.get(1));
            optimisticUnchokingInterval = Integer.parseInt(commonVar.get(2));
            fileName = commonVar.get(3);
            fileSize = Integer.parseInt(commonVar.get(4));
            pieceSize = Integer.parseInt(commonVar.get(5));
            noOfPieces = (int) Math.ceil((fileSize * 1.00) / pieceSize);
        }
    }

    /*
    Function Description: This method reads PeerInfo.cfg file and assigns appropriate values to instance variables of PeerProcess Class

    Parameters: FileName
    */
    public void initPeer(String file) throws IOException {
        Map<Integer, String[]> tempMap = new HashMap<Integer, String[]>();
        bitfield = new byte[noOfPieces];
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String myLine = null;
            while ((myLine = bufferedReader.readLine()) != null) {
                String[] myVar = myLine.trim().split(" ");
                if (Integer.parseInt(myVar[0]) == peerID) {
                    myHostName = myVar[1];
                    myPort = Integer.parseInt(myVar[2]);
                    if (Integer.parseInt(myVar[3]) == 1) {
                        for (int i = 0; i < bitfield.length; i++)
                            bitfield[i] = 1;
                        if (raf == null)
                            System.out.println("file pointer missing for initialization");
                        else {
                            //check if the given file meets the secription
                            if (fileSize == raf.length())
                                System.out.println("file found");
                            else {
                                //if file not found genreate one with random bytes
                                byte[] b = new byte[fileSize];
                                new Random().nextBytes(b);
                                raf.seek(0);
                                raf.write(b);
                                System.out.println("intialized file");
//                            raf.close();
                            }
                        }
                    }
                }
                tempMap.put(Integer.parseInt(myVar[0]), myVar);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading file");
            System.exit(0);
        } finally {
            peerInfoList = tempMap;
        }
    }

    /*
    Function
    Description: This method checks whether incoming message is a Handshake Message or Actual Message

    Parameters: Message Object
    Returns: true if it's handshake message object else false

    */
    public boolean isHandshake(Object o) {
        try {
            HandshakeMessage handshakeMessage = (HandshakeMessage) o;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
            Function Description: This method is used to get peerID when supplied with hostname

            Parameters: HostName
            */
    private int getPeerId(String tHost, int tPort) throws IOException, InterruptedException {
//        System.out.println("looking for peer with  host " + tHost + " and port " + tPort);
        for (int mPeerID : peerInfoList.keySet()) {
            if (peerInfoList.get(mPeerID)[1].equals(tHost)) {
//                System.out.println("returning " + mPeerID + " for " + tHost + " " + tPort);
                return mPeerID;
            }
        }
        System.out.println("not peerid found for the connection");
        return -1;
    }

    private void checkToterminate() throws IOException {
        System.out.print("checking to terminate: ");
        boolean toTerminate = true;
        //checking to terminate program
        int i = 0;
        while (i < bitfield.length) {
            if (bitfield[i] == 0) {
                System.out.print(" check 1 fail at index " + i);
                toTerminate = false;
                break;
            }
            i++;
        }
        if (toTerminate) {
            for (int p : connectionThreads.keySet()) {
                byte[] b = connectionThreads.get(p).pBitfield;
                for (int a : b) {
                    if (a == 0) {
                        System.out.print(" check 2 fail for peer " + p);
                        toTerminate = false;
                        break;
                    }
                }
                if (!toTerminate)
                    break;
                else
                    System.out.println("\npeer " + p + " has downloaded file");
            }
        }
        if (toTerminate) {
            System.out.println("terminating");
            for (int a : bitfield)
                if (a!=1)
                    System.out.print("Incomplete File");
            System.out.println();
            //terminate client
            raf.close();
            System.out.println("done");
            System.exit(23);
        }
        System.out.println();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("You need to pass peerID as argument.");
            System.exit(0);
        } else {
//            System.out.println("peer id given " + args[0]);
            PeerProcess myPeer = new PeerProcess(args[0]);
        }
    }

    /*
    Class Description: This class uses a thread to maintain a connection between 2 peers.
    */
    class Threader extends Thread {

        int peer;
        byte[] pBitfield;
        boolean sendHS;

        private boolean unchoked;
        private boolean interested;

        private boolean pUnchoked;
        private boolean pInterested;


        private Object message;
        private Socket connection;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public Threader(Socket connection, int peer, boolean sendHS) {
            System.out.println("trying to setup connection with peer " + peer + " with HS " + sendHS);
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                System.out.println("input and output stream set");
            } catch (Exception e) {
                System.out.println("error connecting with peer " + peer);
                e.printStackTrace();
            }

            this.connection = connection;
            this.peer = peer;
            this.sendHS = sendHS;
            pBitfield = new byte[noOfPieces];

            if (!connectedPeers.contains(peer))
                connectedPeers.add(peer);

            if (sendHS) {
                sendMessage((Object) new HandshakeMessage(peer));
                System.out.println("hs Sent to " + peer);
            }
        }

        public void run() {
            while (true) {
                try {
                    Object msg = in.readObject();
                    if (isHandshake(msg)) {
                        //The message is handshake message.
                        System.out.println("hs recieved");
                        //If handshake message has been sent, then send Bitfield Message.
                        //Else send Hanshake Message.
                        if (sendHS)
                            sendBitfieldMesssage();
                        else
                            sendMessage((Object) new HandshakeMessage(peer));
                    } else {
                        //The message is actual message
                        Message rcvpkt = new Message(msg);
                        byte mtype = rcvpkt.getMessageType();
                        int type = mtype;
//                        System.out.println("processing type " + type + " " + typeDesc.get("" + type) + " msg");

                        Type1Message rcvpkt1 = null;
                        Type2Message rcvpkt2 = null;
                        Type3Message rcvpkt3 = null;
                        if (type == 4 || type == 6) {
                            rcvpkt2 = new Type2Message(msg);
                        }

                        if (type == 7) {
                            rcvpkt3 = new Type3Message(msg);
                            //System.out.println("RCVPKT3"+rcvpkt3.getPieceIndex());
                        }

                        if (type == 5) {
                            rcvpkt1 = new Type1Message(msg);
                        }

                        switch (type) {
                            case 0:
                                //choke
                                unchoked = false;
                                logFile.writeToLogFile(4, peer);
                                break;

                            case 1:
                                //unchoke
                                unchoked = true;
                                logFile.writeToLogFile(3, peer);
                                checkIfInterested();
                                if (interested)
                                    sendRequestMessage();
                                break;

                            case 2:
                                //interested
                                pInterested = true;
                                interestedPeersInMe.add(peer);
                                logFile.writeToLogFile(5, peer);
                                break;

                            case 3:
                                //not interested
                                pInterested = false;
                                if (interestedPeersInMe.contains(peer))
                                    interestedPeersInMe.remove((Integer) peer);
//                                System.out.println("pbitfield now : ");
//                                for (int i : pBitfield)
//                                    System.out.print(i + " ");
//                                System.out.println();
                                logFile.writeToLogFile(6, peer);
                                break;

                            case 4:
                                //have
                                int tPieceIndex = new Type2Message(msg).getMessagePayload();
                                logFile.writeToLogFile(0, peer, tPieceIndex);
                                pBitfield[tPieceIndex] = 1;
                                System.out.println("Have for "+tPieceIndex);
                                checkIfInterested();
                                break;

                            case 5:
                                //bitfield
                                pBitfield = rcvpkt1.getMessagePayload();
                                System.out.println("bitfield set");
                                if (!sendHS) {
                                    System.out.println("Sending back bitfield\n");
                                    sendBitfieldMesssage();
                                }
                                checkIfInterested();
                                break;

                            case 6:
                                //request
                                int tIndex = new Type2Message(msg).getMessagePayload();
                                System.out.println("Request for "+ tIndex);
                                if (bitfield[tIndex] == 1 && pInterested == true && pUnchoked == true) {
                                    byte[] pieceData;
                                    if (tIndex == noOfPieces - 1){
                                        pieceData = new byte[fileSize%pieceSize];
                                        System.out.println("The last piece is being sent with length "+pieceData.length);
                                    }
                                    else
                                        pieceData = new byte[pieceSize];
                                    try {
                                        raf.seek((tIndex) * pieceSize);
                                        raf.read(pieceData);
                                    } catch (Exception e) {
                                        System.out.println("problem accessing the file");
                                        e.printStackTrace();
                                        break;
                                    }
                                    sendMessage(new Type3Message((byte) 7, tIndex, pieceData));
                                    System.out.println("\nSent " + tIndex);
                                    packetsSent.put(peer, packetsSent.get(peer) + 1);
                                } else {
                                    System.out.println("received incorrect request msg  " + bitfield[tIndex] + "    " + pInterested + "     " + pUnchoked);
                                }
                                break;

                            case 7:
                                //piece
                                //writePieceToFile
                                int tempPieceIndex = rcvpkt3.getPieceIndex();
                                byte[] tempPayload = rcvpkt3.getMessagePayload();
                                System.out.println("Received ");
                                try {
                                    raf.seek(tempPieceIndex * pieceSize);
                                    raf.write(tempPayload);
                                    bitfield[tempPieceIndex] = 1;
                                    piecesDownloaded++;
                                    logFile.writeToLogFile(0, peer, tempPieceIndex, piecesDownloaded);
                                    if (piecesDownloaded == noOfPieces) {
                                        logFile.writeToLogFile();
                                    }
                                } catch (Exception e) {
                                    System.out.println("error writing piece to file");
                                    e.printStackTrace();
                                }
                                //send have msg to every connected peer
                                Type2Message tempHaveMessage = new Type2Message((byte) 4, tempPieceIndex);
                                for (int p : connectionThreads.keySet()) {
                                    connectionThreads.get(p).sendMessage(tempHaveMessage);
                                }

                                checkIfInterested();
                                if (interested && unchoked) {
                                    sendRequestMessage();
                                }
                                break;

                            default:
                                System.out.println("Something is Weird");
                        }
                    }


                } catch (Exception e) {
                    try {
                        checkToterminate();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                    System.out.println("Error! Issue with some connection. Quiting!\n last bitfield :");
                    for (int a : bitfield) {
                        if (a % 10 == 0)
                            System.out.println();
                        System.out.print(a + " ");
                    }
                    System.out.println();
                    break;
                }
            }

        }

        private void sendRequestMessage() {
            ArrayList<Integer> piecesToRequest = new ArrayList<>();
            for (int i = 0; i < noOfPieces; i++) {
                if (bitfield[i] == 0 && pBitfield[i] == 1)
                    piecesToRequest.add(i);
            }
            Collections.shuffle(piecesToRequest);
            int requestIndex = piecesToRequest.get(0);
            Message reqMsg = new Type2Message((byte) 6, requestIndex);
            sendMessage(reqMsg);
        }

        private void checkIfInterested() {
            for (int i = 0; i < noOfPieces; i++) {
                if (bitfield[i] == 0 && pBitfield[i] == 1) {
                    if (!interested) {
                        interested = true;
                        //send interested mesaage
                        System.out.println("updating to interested");
                        Message interestedMsg = new Message((byte) 2);
                        sendMessage(interestedMsg);
                    }
                    return;
                }
            }
            if (interested) {
                interested = false;
                System.out.println("updating to not interested");
                Message notInterestedMsg = new Message((byte) 3);
                sendMessage(notInterestedMsg);
                return;
            }
            System.out.println();
        }


        private void sendBitfieldMesssage() {
            Type1Message tempBitfieldMessage = new Type1Message(noOfPieces + 1, (byte) 5, bitfield);
            sendMessage(tempBitfieldMessage);
        }

        // send a message to the output stream
        public void sendMessage(Object msg) {
            try {
                out.writeObject(msg);
                out.flush();
//                System.out.println("Send message: " + msg + " to Client ");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    /*
    Class Description: This class uses a thread for the purpose of socket programming.
    */
    class Listener extends Thread {
        Socket connection;
        ServerSocket listener = new ServerSocket(myPort);

        Listener() throws IOException {
//            System.out.println("Listener on");
        }

        @Override
        public void run() {
            boolean acceptNew = true;
            while (acceptNew) {
                try {
//                    System.out.println("\nwaiting for accept");
                    connection = listener.accept();
//                    System.out.println("got accept\n");
                } catch (IOException e) {
                    System.out.println("exception in accept()");
                    e.printStackTrace();
                    acceptNew = false;
                }
                String tHost = connection.getInetAddress().getHostAddress();
                int tPort = connection.getPort();
                int tPeerID = 0;
                try {
                    tPeerID = getPeerId(tHost, tPort);
                } catch (IOException e) {
                    e.printStackTrace();
                    acceptNew = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    acceptNew = false;
                }

                if (tPeerID != -1) {
                    logFile.writeToLogFile(1, tPeerID);
                    Threader temp = new Threader(connection, tPeerID, false);
                    connectionThreads.put(tPeerID, temp);
                    temp.start();
                }
            }
        }
    }

    class NeighbourTimer extends TimerTask {

        @Override
        public void run() {
            completeTask();
        }

        private void completeTask() {

            if (connectedPeers.isEmpty()) {
                return;
            }

            Message chokeMsg;

            //saving old pref neighbours
            ArrayList<Integer> oldPrefNeighbours = new ArrayList<>(prefNeighbours);

            System.out.println("refreshing Neightbours");
            //sorting the peers based on pieces downloaded
            if (packetsSent == null) {
                return;
            }

            //shuffling the packetsent Hashmap
            List<Integer> list = new ArrayList<>(packetsSent.keySet());
            Collections.shuffle(list);
            Map<Integer, Integer> shuffledPacketsSent = new LinkedHashMap<>();
            list.forEach(k->shuffledPacketsSent.put(k, packetsSent.get(k)));


            Map<Integer, Integer> packetsSentSorted = shuffledPacketsSent
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(
                            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                    LinkedHashMap::new));

            for( int p : packetsSent.keySet()){
                packetsSentSorted.put(p,0);
            }

            prefNeighbours.clear();
            //setting preferred neighbours
            Iterator<Integer> sortedPeerSetIterator = packetsSentSorted.keySet().iterator();
            for (int i = 0; i < numberOfPreferredNeighbours; i++) {
                if (sortedPeerSetIterator.hasNext()) {
                    int currentPeer = sortedPeerSetIterator.next();
                    if (connectionThreads.get(currentPeer) != null) {
                        prefNeighbours.add(currentPeer);
                    }
                }
            }

            logFile.writeToLogFile(prefNeighbours);

            chokeMsg = new Message((byte) 0);
            //send choke neighbours to peers that are not preferred neighbours now
            for (int p : oldPrefNeighbours) {
                if (!prefNeighbours.contains(p)) {
                    //send choke msg
                    connectionThreads.get(p).sendMessage(chokeMsg);
                    connectionThreads.get(p).pUnchoked = false;
                }
            }

            Message unchokeMsg = new Message((byte) 1);
            //sending unchoked to new prefered neighbours
            for (Integer p : prefNeighbours) {
                if (!oldPrefNeighbours.contains(p) && p!=optimisticNeighbour) {
                    System.out.println("new pref neighbour " + p);
                    connectionThreads.get(p).sendMessage(unchokeMsg);
                    connectionThreads.get(p).pUnchoked = true;
                }
            }

            System.out.print("\n\nconnected peers   " + connectedPeers.size());
            System.out.print("    running threaders " + connectionThreads.size());
            System.out.print("    pref neighbours " + prefNeighbours.size() + "\n");

            try {
                PeerProcess.this.checkToterminate();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class OptimisticTimer extends TimerTask {

        @Override
        public void run() {
            completeTask();
        }

        private void completeTask() {
//            System.out.println("timer2 task running");
            if (interestedPeersInMe.isEmpty())
                return;
            Set<Integer> candidates = new HashSet<Integer>(interestedPeersInMe);
            candidates.removeAll(prefNeighbours);
            if (optimisticNeighbour != -1)
                candidates.remove(optimisticNeighbour);
            //candidates is list of peers that are interested and not preferred neighbours
            List l = new ArrayList(candidates);
            Collections.shuffle(l);
            int count = 0;
            while (count < l.size()) {
                if (!prefNeighbours.contains(l.get(count))) {
//                    prefNeighbours.add((int) l.get(count));
                    optimisticNeighbour = (int) l.get(count);
                    //send unchoke to peer
                    logFile.writeToLogFile(2, optimisticNeighbour);
                    System.out.println("peer " + l.get(count) + " is new optimistic unchoked neighbour");
                    Message unchokeMsg = new Message((byte) 1);
                    connectionThreads.get(l.get(count)).sendMessage(unchokeMsg);
                    connectionThreads.get(l.get(count)).pUnchoked = true;
                    break;
                }
                count++;
            }

            System.out.print("\n\nconnected peers   " + connectedPeers.size());
            System.out.print("    running threaders " + connectionThreads.size());
            System.out.print("    pref neighbours " + prefNeighbours.size() + "\n");

        }
    }

}