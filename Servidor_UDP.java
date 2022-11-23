package servidor_udp;

/**
 *
 * @author Leonardo
 */
import java.net.*;
import java.io.*;

public class Servidor_UDP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            DatagramSocket serverSocket = new DatagramSocket(9870);
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            System.out.println("SERVIDOR LIGADO!!!");

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String sentence = new String(receivePacket.getData());
                System.out.println("RECEBIDO: " + sentence + ".");

                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                String capitalizedSentence = "MSG DO SERVIDOR: " + sentence;
                sendData = capitalizedSentence.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);

                receiveData = new byte[1024];

                if (sentence.equals("fim")) {
                    serverSocket.close();
                    break;
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }

    }

}
