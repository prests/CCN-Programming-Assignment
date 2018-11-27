import java.io.*;
import java.net.*;
import java.util.*;

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
        DatagramSocket client = new DatagramSocket();
        client.setSoTimeout(retransmissionTimer);
        //while(true)
        //{
            //Sending a message
            //Parse inputfile
            File file = new File("inputfile.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String finalst = "";
            String st;
            while((st = br.readLine()) != null)
            {
                finalst += st + '\n';
            }

            //convert string to bytes
            byte[] bytest = finalst.getBytes("UTF-8");
            System.out.println(bytest.length);
            System.out.println(bytest[1]);

            //section bytes off into groups of 512
            byte[][] packets = new byte[(bytest.length/packetLimit)+1][];
            System.out.println(bytest.length/packetLimit);
            for(int i=0; i<(bytest.length/packetLimit); ++i){
                System.out.println(i);
                packets[i] = Arrays.copyOfRange(bytest, i*packetLimit, (i+1)*packetLimit);
                System.out.println("Size: " + String.valueOf(packets[i].length));
                System.out.println("Value: " + String.valueOf(packets[i]));
            }
            if(bytest.length % packetLimit == 0){
                packets[packets.length-1] = new byte[0];
            }
            else
            {
                System.out.println("Here");
                System.out.println(bytest.length % packetLimit);
                packets[packets.length-1] = Arrays.copyOfRange(bytest, bytest.length-(bytest.length % packetLimit), bytest.length-1);
            }
            System.out.println(packets[0]);
            //System.out.println(packets[0][1]);

            //Connection and Send
            int seq = 0;
            int ack = 0;
            for(int i=0; i<packets.length; ++i)
            {
                DatagramPacket send = new DatagramPacket(packets[i], packets[i].length, address, port);
                client.send(send);

                byte[] seqBytes = Integer.toString(seq).getBytes();
                DatagramPacket seqPacket = new DatagramPacket(seqBytes, 1, address, port);
                client.send(seqPacket);

                DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);
                client.receive(ackPacket);
                ack = Integer.parseInt(String.getData(ackPacket));

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
            }
        //}
    }
}