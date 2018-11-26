import java.io.*;
import java.net.*;
import java.util.*;

public class sftpClient
{
    private static final int packetLimit = 512;
    public static void main(String[] args) throws Exception
    {
        if(args.length != 1) {
            System.out.println("Required arguments: hostname");
            return;
        }
        int port = 9093;
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramSocket client = new DatagramSocket();
        //while(true)
        //{
            File file = new File("inputfile.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String finalst = "";
            String st;
            while((st = br.readLine()) != null)
            {
                finalst += st + '\n';
            }
            byte[] bytest = finalst.getBytes("UTF-8");
            System.out.println(bytest.length);

            //section bytes off into groups of 512
            byte[][] packets = new byte[(bytest.length/packetLimit)+1][];
            for(int i=0; i<bytest.length/packetLimit; ++i){
                System.out.println(i);
                packets[i] = Arrays.copyOfRange(bytest, i*packetLimit, (i+1)*packetLimit);
                System.out.println("Size:" + String.valueOf(packets[i].length));
            }
            System.out.println(packets.length);
        //}
    }
}