import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class gftpClient
{
    private static final int packetLimit = 512;
    private static final int retransmissionTimer = 1000; //milliseconds
    private static final int retransmissionMaxCount = 5;
    public static void main(String[] args) throws Exception
    {
        //Making sure proper arguments are setup
        if(args.length != 1) {
            System.out.println("Required arguments: hostname");
            return;
        }
        //Connection information
        InetAddress address = InetAddress.getByName(args[0]);
        int port = 9093;
        DatagramSocket client = new DatagramSocket(5555);//port 5555
        client.setSoTimeout(retransmissionTimer); //setting timeout

        while(true)//maintain connection
        {
            /*
             * Sending a file to the server
             */
            
            //Parse inputfile
            Path filelocation = Paths.get("inputfile.txt");
            byte[] message = Files.readAllBytes(filelocation);

            //section bytes off into groups of 512
            byte[][] packets = new byte[(message.length/packetLimit)+1][];
            for(int i=0; i<(message.length/packetLimit); ++i){
                packets[i] = Arrays.copyOfRange(message, i*packetLimit, (i+1)*packetLimit);
            }
            if(message.length % packetLimit == 0){
                packets[packets.length-1] = new byte[0];
            }
            else
            {
                packets[packets.length-1] = Arrays.copyOfRange(message, message.length-(message.length % packetLimit), message.length);
            }

            boolean[] ackPackets = new boolean[packets.length];
            Arrays.fill(ackPackets, false);
            int[] triesOnPacket = new int[packets.length];
            Arrays.fill(triesOnPacket, 0);
            //Connection and Send
            int window = 4;
            int sequenceBase = 0;
            int sequenceMax = sequenceBase + window;

            int seq = 0;
            int ack = 0;
            int count = 0; //# of timeouts
            long timeStart = System.currentTimeMillis();
            System.out.println("# OF PACKETS: " + String.valueOf(packets.length));
            for(int i=0; i<packets.length; ++i)
            {                
                if(triesOnPacket[sequenceBase] > 5)
                {
                    System.out.println("sFTP: file transfer unsuccessful: packet retansmission limit reached");
                    return;
                }

                long checkTime = (System.currentTimeMillis() - timeStart); 
                System.out.println("CheckTime: " + String.valueOf(checkTime));
                if(sequenceBase < checkTime/1000)
                {
                    System.out.println("base sequence lost");
                    i = sequenceBase-1;
                    seq = sequenceBase;
                    for(int j=sequenceBase; j<sequenceBase+sequenceMax+1; ++j){
                        ackPackets[i] = false;
                    }
                    ++triesOnPacket[i];
                    while((System.currentTimeMillis()-timeStart)/1000 >= sequenceBase){
                        timeStart += 1000;
                    }
                    continue;
                }

                //Create and send data
                System.out.println("i: " + String.valueOf(i) + " seq: " + String.valueOf(seq) + " seqMax: " + String.valueOf(sequenceMax) + " seqBase: " + String.valueOf(sequenceBase));
                if(i<sequenceMax)
                {
                    System.out.println("Sending");
                    DatagramPacket send = new DatagramPacket(packets[i], packets[i].length, address, port);
                    client.send(send);

                    //Create and send SEQ # in 1 byte packet
                    System.out.println("IDK: " + Integer.toString(seq));
                    byte[] seqBytes = Integer.toString(seq).getBytes();
                    DatagramPacket seqPacket = new DatagramPacket(seqBytes, seqBytes.length, address, port);
                    client.send(seqPacket);
                    ++seq;
                }
                else{

                    //Receive ACK packet from server
                    DatagramPacket ackPacket = new DatagramPacket(new byte[3], 3);
                    try
                    {
                        client.receive(ackPacket); 
                    }
                    catch(SocketTimeoutException e) 
                    { //Took too long to receive ACK
                        System.out.println("No packets from server in a while");
                         //move i back to resend packet again
                        
                        continue; //try again
                    }

                    //convert ACK packet to Integer
                    byte[] ackBytes = ackPacket.getData();
                    ByteArrayInputStream bais = new ByteArrayInputStream(ackBytes);
                    InputStreamReader isr = new InputStreamReader(bais);
                    BufferedReader br = new BufferedReader(isr);
                    String ackString = br.readLine();
                    ack = Integer.parseInt(ackString.trim());
                    System.out.println("\nACK: " + String.valueOf(ack) + "\n");
                    ackPackets[ack] = true;
                    if(ack == sequenceBase){
                        System.out.println("moving");
                        ++sequenceBase;
                        ++sequenceMax;
                    }
                    --i;
                    //rdt logic (alternating ACK and SEQ)
                }
                
            }
            long timeEnd = System.currentTimeMillis();
            System.out.println("sFTP: file sent successfully to " + String.valueOf(args[0]) + " in " + String.valueOf((timeEnd-timeStart)/1000) + " secs");
            client.close(); //close connection
            break; //finish
        }
    }
}