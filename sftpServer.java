import java.io.*;
import java.net.*;
import java.util.*;

/*
 *
 */
public class sftpServer
{
    private static final double LOSS_RATE = 0.2;
    private static final int AVERAGE_DELAY = 100; //milliseconds
    public static void main(String[] args) throws Exception
    {
        //Clear the output file at start
        PrintWriter clear = new PrintWriter("outputfile.txt");
        clear.print("");
        clear.close();

        int port = 9093;
        
        Random random = new Random();
        
        DatagramSocket server = new DatagramSocket(port);
        /*
         * Receiving a file from client
         */
        while(true)
        {
            int seq = 0;
            int ack = 0;

            //Receiving a message
            DatagramPacket request = new DatagramPacket(new byte[512], 512);
            server.receive(request);

            //Receiving ACK from client
            DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);
            server.receive(ackPacket);

            //Convert ACK packet to integer
            byte[] ackBytes = ackPacket.getData();
            ByteArrayInputStream bais = new ByteArrayInputStream(ackBytes);
            InputStreamReader isr = new InputStreamReader(bais);
            BufferedReader br = new BufferedReader(isr);
            String ackSting = br.readLine();
            ack = Integer.parseInt(ackSting);

            //Move Sequence along
            seq = ack;

            //Randomization to see if packet is "lost"
            if(random.nextDouble() < LOSS_RATE)
            {
                System.out.println("Reply not sent.");
                continue;
            }

            printData(request);//Write data of packet to file

            Thread.sleep((int) (random.nextDouble()*2*AVERAGE_DELAY)); //Sleep for "delay" of packet

            //connection setup for SEQ response
            InetAddress clientHost = request.getAddress();
            int clientPort = request.getPort();

            //Convert Seq int to Packet
            byte[] seqBytes = Integer.toString(seq).getBytes();
            DatagramPacket seqPacket = new DatagramPacket(seqBytes , 1, clientHost, clientPort);
            server.send(seqPacket);
            System.out.println("Reply sent.");

            if(request.getLength() != 512) //checks for last packet
            {
                System.out.println("Full file received");
                server.close(); //close connection
                break; //finish
            }
        }
    }



    private static void printData(DatagramPacket request) throws Exception
    {
        //Convert data packet to string and write to file
        String val = new String(request.getData(), 0, request.getLength());
        FileWriter fw = new FileWriter("outputfile.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        out.print(val);
        out.close();
    }
}