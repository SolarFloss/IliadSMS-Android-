package application.networking;

import com.sun.org.apache.regexp.internal.RE;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by Nicholas on 1/2/2017.
 */
public class Recieving implements Runnable{
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private final int PORT = 13579;
    private static ObjectInputStream objectInputStream;
    private String[] info;
    private String type,number,person,body,date,currentTime;
    private int hour,minute,second;
    private static boolean running = true;
    private static String state = null;
    private String previousSent;
    private String previousReceived;


    public static void setRunning(boolean value){
        running = value;
    }
    public static String getState(){return state;}


    @Override
    public void run() {
        System.out.println("Spinning up");
        while(running){

            setupConnection();

            if(running)
                setupStreams();

            if(running)
                receiveData();
        }
        close();
    }

    private void setupConnection(){
        try {
            state = "Connection";
            serverSocket = new ServerSocket(PORT);
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    private void setupStreams(){
        try {
            state = "Streams";
            //dataInputStream = new DataInputStream(clientSocket.getInputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            close();
        }
    }


    private void receiveData(){
        state = "Data";
        try {

            boolean done = false;
            while(!done){
                //Array: Type, Number, Person, Body
                info = (String[])objectInputStream.readObject();

                this.type = info[0];
                this.number = info[1];
                this.person = info[2];
                this.body = info[3];

                hour = Calendar.getInstance().get(Calendar.HOUR);
                minute = Calendar.getInstance().get(Calendar.MINUTE);
                second = Calendar.getInstance().get(Calendar.SECOND);
                currentTime = String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second);

                //System.out.println(this.type);

                if(!body.equals(previousReceived)) {
                    if (type.equals("received")) {
                        System.out.println(type + " from: " + person + "\n" + body + "\n" + currentTime + "\n--------------------------------");
                        previousReceived = body;
                    } else if (type.equals("received") && person.equals("Unsaved Number")){
                        System.out.println(type + " from: " + number  + "\n" + body + "\n" + currentTime + "\n--------------------------------");
                        previousReceived = body;
                    }
                }

                /*else if(type.equals("sent") & !body.equals(previousSent)){
                    System.out.println(type + " to: " + person + "\n" + body + "\n" + currentTime + "\n--------------------------------");
                    previousSent = body;
                }
                */



                done = true;
            }
            //System.out.println("Done");
            serverSocket.close();
            clientSocket.close();
            objectInputStream.close();

        } catch (Exception e) {
            close();
        }

    }

    public static void close(){
        try {
            if(clientSocket != null)
                clientSocket.close();
            if(objectInputStream != null)
                objectInputStream.close();
            if(serverSocket != null)
                serverSocket.close();

            running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
