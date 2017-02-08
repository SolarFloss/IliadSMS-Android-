package com.solarfloss.iliadsms.networking;

import android.os.AsyncTask;
import android.util.Log;

import com.solarfloss.iliadsms.activities.Start;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Nicholas on 1/4/2017.
 */

public class Authenticate extends AsyncTask{
    private String code,localIP,deviceIP;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private DataInputStream dataInputStream;
    private final int PORT = 24680;
    private boolean done = false;
    private boolean success = false;
    private final String TAG = "Authenticate";
    private String phoneNumber;
    private String[] info;



    public Authenticate(String code,String localIP,String deviceIP,String phoneNumber){
        this.code = code;
        this.deviceIP = deviceIP;
        this.localIP = localIP;
        this.phoneNumber = phoneNumber;

        info = new String[]{phoneNumber,localIP};
    }

    private void send(){
        try{
            socket = new Socket(deviceIP,PORT);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(info);
            socket.close();
            objectOutputStream.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receive(){
        try {
            Log.e(TAG,"Waiting for response from Odyssey");
            serverSocket = new ServerSocket(PORT);
            socket = serverSocket.accept();

            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        while(!done){
            try {
                if(dataInputStream != null)
                    success = dataInputStream.readByte() == 1;


                socket.close();
                dataInputStream.close();
                done = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(){
        try {
            serverSocket.close();
            socket.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        send();
        Log.e(TAG,"Sent");
        //Now wait for response
        receive();
        Start.authSuccessful();
        return null;
    }
}
