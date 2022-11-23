package cliente;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// First, we create the ClipboardListener to listen on the machine new things copied
// Our listener will extends Thread to run in background
public class ClipboardListener extends Thread implements ClipboardOwner { //Cria a Classe, e extende as interfaces thread e ClipboardOwner

    // Entry Listener. It will be useful for class wanting to be alerted when a new entry is copied
    interface EntryListener {

        void onCopy(String data);
    }

    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); // Criar uma vaŕiavel recebendo o metodo
    private EntryListener entryListener;

    public EntryListener getEntryListener() {
        return entryListener;
    }

    public void setEntryListener(EntryListener entryListener) {
        this.entryListener = entryListener;
    }

    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        try {
            sleep(200);
        } catch (Exception e) {
        }

        Transferable contents = c.getContents(this);
        processContents(contents);
        regainOwnership(c, contents);
    }

    public void processContents(Transferable t) {
        try {
            String what = (String) (t.getTransferData(DataFlavor.stringFlavor));
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");

            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence;

            while (true) {
                sendData = what.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9870);
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("RESPOSTA DO SERVIDOR: " + modifiedSentence);
                if (what.equals("fim"));
                {
                    clientSocket.close();
                    break;
                }
            }

            // we alert our entry listener
            if (entryListener != null) {
                entryListener.onCopy(what);
            }
        } catch (Exception e) {
        }
    }

    public void regainOwnership(Clipboard c, Transferable t) {
        c.setContents(t, this);
    }

    public void run() {
        Transferable transferable = clipboard.getContents(this);
        regainOwnership(clipboard, transferable);
        while (true);
    }
}
