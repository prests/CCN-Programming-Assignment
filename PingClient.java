import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Client to ping server over UDP
 */
public class PingClient
{
    public static void main(String[] args) throws Exception
    {
        int sequenceNum = 0;
        if(args.length != 2) {
            System.out.println("Required arguments: hostname, port");
            return;
        }
        int port = Integer.parseInt(args[1]);
        InetAddress address = InetAddress.getByName(args[0]);

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(1000);
        while(sequenceNum<5){
            DatagramPacket send = new DatagramPacket(new byte[56], 56, address, port);
            long time1 = System.currentTimeMillis();
            socket.send(send);
            
            DatagramPacket receive = new DatagramPacket(new byte[56], 56);
            while(true){
                try{
                    socket.receive(receive);
                    long time2 = System.currentTimeMillis();
                    long timeTotal = time2-time1;
                    System.out.println("PING " + address.toString().substring(1,address.toString().length()) + " " + String.valueOf(sequenceNum) + " " + String.valueOf(timeTotal));
                    sequenceNum = sequenceNum + 1;
                    break;
                }
                catch(SocketTimeoutException e){
                    System.out.println("PING " + address.toString().substring(1,address.toString().length()) + " " + String.valueOf(sequenceNum) + " LOST");
                    break;
                }                
            }
        }
    }
}