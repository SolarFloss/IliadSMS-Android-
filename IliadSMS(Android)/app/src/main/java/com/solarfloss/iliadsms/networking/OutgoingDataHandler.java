package com.solarfloss.iliadsms.networking;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Nicholas on 12/31/2016.
 */

public class OutgoingDataHandler extends AsyncTask{
    private String[] info;
    private ObjectOutputStream objectOutputStream;
    private final int PORT = 13579;
    private String type,number,person,body;
    private String TAG = "Outgoing";
    private Socket socket;
    private static String previousMessage = "";

    public OutgoingDataHandler(String[] info){
        this.info = info;
    }

    private void sendToPC(){
        try{

            previousMessage = info[3];
            //socket = new Socket(DeviceSearch.getWantedDeviceIp(),PORT);
            socket = new Socket();
            socket.connect(new InetSocketAddress(DeviceSearch.getWantedDeviceIp(), PORT), 1000);
            //socket.setSoTimeout(1000);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(info);
            socket.close();
            objectOutputStream.close();

            //Log.e(TAG,"Sent to device: " + info[0]);



        } catch (UnknownHostException e) {
            e.printStackTrace();
            closeAll();
        } catch(SocketTimeoutException e){
            Log.e(TAG,"Timed out");
            closeAll();
        } catch (IOException e) {
            e.printStackTrace();
            closeAll();
        }
    }

    private void closeAll(){
        try {
            if (socket != null)
                socket.close();

            if(objectOutputStream != null)
                objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected Object doInBackground(Object[] objects) {
        sendToPC();
        return null;
    }
}
