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
        int port = 9093;
        
        Random random = new Random();
        
        DatagramSocket server = new DatagramSocket(port);
        //set Timeout
        while(true)
        {
            int seq = 0;
            int ack = 0;
            //Receiving a message
            DatagramPacket request = new DatagramPacket(new byte[512], 512);
            server.receive(request);

            DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);
            server.receive(ackPacket)
            ack = Integer.parseInt(ackPacket.getData());
            seq = ack;

            printData(request);

            if(random.nextDouble() < LOSS_RATE)
            {
                System.out.println("Reply not sent.");
                continue;
            }

            Thread.sleep((int) (random.nextDouble()*2*AVERAGE_DELAY));

            InetAddress clientHost = request.getAddress();
            int clientPort = request.getPort();


            byte[] seqBytes = seq.toString().getBytes();
            DatagramPacket seqPacket = new DatagramPacket(seqBytes , 1, clientHost, clientPort);
            server.send(seqPacket);
            System.out.println("Reply sent.");
        }
    }



    private static void printData(DatagramPacket request) throws Exception
    {
        // Obtain references to the packet's array of bytes.
        byte[] buf = request.getData();
        System.out.println(buf);

        // Wrap the bytes in a byte array input stream,
        // so that you can read the data as a stream of bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);

        // Wrap the byte array output stream in an input stream reader,
        // so you can read the data as a stream of characters.
        InputStreamReader isr = new InputStreamReader(bais);

        // Wrap the input stream reader in a bufferred reader,
        // so you can read the character data a line at a time.
        // (A line is a sequence of chars terminated by any combination of \r and \n.) 
        BufferedReader br = new BufferedReader(isr);

        // The message data is contained in a single line, so read this line.
        String line = br.readLine();

        // Print host address and data received from it.
        System.out.println(
            "Received from " + 
            request.getAddress().getHostAddress() + 
            ": " +
            new String(line) );
    }
}