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
        if(args.length != 1) {
            System.out.println("Required arguments: hostname");
            return;
        }
        InetAddress address = InetAddress.getByName(args[0]);
        int port = 9093;
        DatagramSocket client = new DatagramSocket(5555);
        client.setSoTimeout(retransmissionTimer);
        while(true)
        {
            //Sending a message
            //Parse inputfile
            Path filelocation = Paths.get("inputfile.txt");
            byte[] message = Files.readAllBytes(filelocation);
            String temp = new String(message);
            System.out.println(temp);
            //section bytes off into groups of 512
            byte[][] packets = new byte[(message.length/packetLimit)+1][];
            System.out.println(message.length/packetLimit);
            for(int i=0; i<(message.length/packetLimit); ++i){
                System.out.println(i);
                packets[i] = Arrays.copyOfRange(message, i*packetLimit, (i+1)*packetLimit);
                System.out.println("Size: " + String.valueOf(packets[i].length));
                System.out.println("Value: " + String.valueOf(packets[i]));
                String val = new String(packets[i]);
                System.out.println("String: " + val);
            }
            if(message.length % packetLimit == 0){
                packets[packets.length-1] = new byte[0];
            }
            else
            {
                System.out.println("Here");
                System.out.println(message.length % packetLimit);
                packets[packets.length-1] = Arrays.copyOfRange(message, message.length-(message.length % packetLimit), message.length);
                String val = new String(packets[packets.length-1]);
                System.out.println("String: " + val);
            }
            System.out.println(packets[0]);
            System.out.println(packets[0].length);


            //Connection and Send
            int seq = 0;
            int ack = 0;
            int count = 0;
            System.out.println(packets[0].getClass().getName());
            for(int i=0; i<packets.length; ++i)
            {
                if(count > 5)
                {
                    System.out.println("sFTP: file transfer unsuccessful: packet retansmission limit reached");
                }
                if(i==packets.length-1)
                {
                    System.out.println("last packet size: " + String.valueOf(packets[i].length));
                }
                DatagramPacket send = new DatagramPacket(packets[i], packets[i].length, address, port);
                System.out.println(send.getClass().getName());
                client.send(send);

                byte[] seqBytes = Integer.toString(seq).getBytes();
                DatagramPacket seqPacket = new DatagramPacket(seqBytes, 1, address, port);
                client.send(seqPacket);//System.out.println(packets[0][1]);

                DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);
                try{
                    client.receive(ackPacket);
                }
                catch(SocketTimeoutException e){
                    System.out.println("Packet lost");
                    --i;
                    ++count;
                    continue;
                }
                count = 0;
                byte[] ackBytes = ackPacket.getData();
                ByteArrayInputStream bais = new ByteArrayInputStream(ackBytes);
                InputStreamReader isr = new InputStreamReader(bais);
                BufferedReader br = new BufferedReader(isr);
                String ackString = br.readLine();
                ack = Integer.parseInt(ackString);

                if(ack == seq)
                {
                    if(seq == 0){
                        seq = 1;
                    }
                    else
                    {
                        seq = 0;
                    }
                }
                else
                {
                    //dupliate packet???
                }
                System.out.println(seq);
            }
            //System.out.println("sFTP: file sent successfully to ", "blah", " in ", "blah", " secs");
            Thread.sleep(10000);
        }
    }
}