package com.solarfloss.iliadsms.networking;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import com.solarfloss.iliadsms.activities.Start;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Nicholas on 12/31/2016.
 */

public class IncomingDataHandler extends AsyncTask{
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private final int PORT = 13579;
    private static ObjectInputStream objectInputStream;
    private static String TAG = "Incoming";
    private Context context;
    public static boolean running = true;
    private String message;
    private static boolean debounce = true;
    private static boolean switchDebounce = false;
    private boolean receiveDebounce = false;
    private String number;
    private static String[] info;


    public IncomingDataHandler(boolean value){
        running = value;
        debounce = false;
    }


    public static void setRunning(boolean value){

        running = value;

    }



    private void setupConnection(){
        try{
            serverSocket = new ServerSocket(PORT);
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Connection Broke");
            closeAll(true);
        }
    }

    private void setupStreams(){
        try{
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Stream Broke");
            closeAll(true);
        }
    }



    private void receiveData(){
        boolean done = false;
        //Log.e(TAG,"Received");
        while(!done){
            try {
                if(objectInputStream != null) {
                    info = (String[])objectInputStream.readObject();
                    number = info[1];
                    message = info[0];

                    if(!number.contains("+1") && !number.equals("00001"))
                        number = "+1 " + number;



                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, message, null, null);
                    closeAll(false);
                    //Log.e(TAG,"Sent");

                }
                done = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"Receive Broke");
                done = true;
                closeAll(true);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public static void closeAll(boolean kill){
        try{

            //debounce = false;

            if(kill){
                running = false;
                Start.setDebounce(false);
            }

            if(clientSocket != null && !(clientSocket.isClosed())) {
                clientSocket.close();
                if(kill)
                    Log.e(TAG,"Client Socket Closed");
            }

            if(serverSocket != null && !(serverSocket.isClosed())) {
                serverSocket.close();
                if(kill)
                    Log.e(TAG,"Server Socket Closed");
            }

            if(objectInputStream != null) {
                objectInputStream.close();
                if(kill)
                    Log.e(TAG,"Input Stream Closed");
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void start(){
        if(Start.getWifiState()) {

            while(running) {
                //Log.e(TAG,"Up top");
                setupConnection();

                if(running)
                    setupStreams();

                if(running)
                    receiveData();
            }

            Start.setDebounce(false);
            Log.e(TAG,"Running stopped");
        }else{
            Log.e(TAG,"No connection");
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.e(TAG,"Spinning up");
        start();
        return null;
    }


}
