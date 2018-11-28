import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class sftpClient
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


            //Connection and Send
            int seq = 0;
            int ack = 0;
            int count = 0; //# of timeouts
            long timeStart = System.currentTimeMillis();
            for(int i=0; i<packets.length; ++i)
            {
                if(count > 5)
                {
                    System.out.println("sFTP: file transfer unsuccessful: packet retansmission limit reached");
                    return;
                }
                //Create and send data
                DatagramPacket send = new DatagramPacket(packets[i], packets[i].length, address, port);
                client.send(send);

                //Create and send SEQ # in 1 byte packet
                byte[] seqBytes = Integer.toString(seq).getBytes();
                DatagramPacket seqPacket = new DatagramPacket(seqBytes, 1, address, port);
                client.send(seqPacket);


                //Receive ACK packet from server
                DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);
                try
                {
                    client.receive(ackPacket); 
                }
                catch(SocketTimeoutException e) 
                { //Took too long to receive ACK
                    System.out.println("Packet lost");
                    --i; //move i back to resend packet again
                    ++count;
                    continue; //try again
                }
                count = 0; //reset counter

                //convert ACK packet to Integer
                byte[] ackBytes = ackPacket.getData();
                ByteArrayInputStream bais = new ByteArrayInputStream(ackBytes);
                InputStreamReader isr = new InputStreamReader(bais);
                BufferedReader br = new BufferedReader(isr);
                String ackString = br.readLine();
                ack = Integer.parseInt(ackString);

                //rdt logic (alternating ACK and SEQ)
                if(seq == 0)
                {
                    seq = 1;
                }
                else
                {
                    seq = 0;
                }
            }
            long timeEnd = System.currentTimeMillis();
            System.out.println("sFTP: file sent successfully to " + String.valueOf(args[0]) + " in " + String.valueOf(timeEnd-timeStart) + " secs");
            client.close(); //close connection
            break; //finish
        }
    }
}