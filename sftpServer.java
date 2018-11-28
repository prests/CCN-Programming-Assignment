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
            PrintWriter clear = new PrintWriter("outputfile.txt");
            clear.print("");
            clear.close();
            while(true)
            {
                int seq = 0;
                int ack = 0;

                //Receiving a message
                DatagramPacket request = new DatagramPacket(new byte[512], 512);
                server.receive(request);

                DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);
                server.receive(ackPacket);
                byte[] ackBytes = ackPacket.getData();
                System.out.println("Byte size: " + String.valueOf(ackBytes.length));
                ByteArrayInputStream bais = new ByteArrayInputStream(ackBytes);
                InputStreamReader isr = new InputStreamReader(bais);
                BufferedReader br = new BufferedReader(isr);
                String ackSting = br.readLine();
                ack = Integer.parseInt(ackSting);

                seq = ack;

                if(random.nextDouble() < LOSS_RATE)
                {
                    System.out.println("Reply not sent.");
                    continue;
                }

                printData(request);

                Thread.sleep((int) (random.nextDouble()*2*AVERAGE_DELAY));

                InetAddress clientHost = request.getAddress();
                int clientPort = request.getPort();

                byte[] seqBytes = Integer.toString(seq).getBytes();
                DatagramPacket seqPacket = new DatagramPacket(seqBytes , 1, clientHost, clientPort);
                server.send(seqPacket);
                System.out.println("Reply sent.");

                if(request.getLength() != 512){
                    System.out.println("Full file received");
                    break;
                }
            }
        }
    }



    private static void printData(DatagramPacket request) throws Exception
    {
        // Obtain references to the packet's array of bytes.
        byte[] buf = request.getData();
        String val = new String(buf);
        System.out.println(val);
        FileWriter fw = new FileWriter("outputfile.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        out.print(val);
        out.close();

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