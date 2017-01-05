package application.networking;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Nicholas on 1/2/2017.
 */
public class Sending{
    private static Socket socket;
    private static DataOutputStream dataOutputStream;
    private static final int PORT = 13579;
    private static final String phoneIP = "192.168.1.39";






    public static void send(String message) {
        try {
            socket = new Socket(phoneIP,PORT);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(message);
            socket.close();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }


    public static void close(){
        try {
            if (socket != null)
                socket.close();
            if(dataOutputStream != null)
                dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
