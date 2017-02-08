package com.solarfloss.iliadsms.networking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.solarfloss.iliadsms.activities.Start;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Nicholas on 12/31/2016.
 */

public class DeviceSearch extends AsyncTask<Void, Void, String>{
    private static final String TAG = "DeviceSearch";
    private static String WANTED_DEVICE_NAME = "";
    private static String WANTED_DEVICE_IP = null;
    private Context context;
    private Start start;
    private String localIP;
    private static String subnet = null;
    private static boolean success = false;
    private static boolean ready = false;
    public static boolean isReady(){return ready;}
    //private Start start = new Start();
    public static void setReady(boolean value){ready = value;}


    public static String getWantedDeviceIp() {
        return WANTED_DEVICE_IP;
    }

    //Get phone's subnet
    private static String getSubnet(){
        if(getLocalIP() != null){
            String ip = getLocalIP();
            //Log.e(TAG,"Local IP is: " + ip);
            //Log.e(TAG,"Subnet is: " + ip.substring(0,ip.lastIndexOf(".")));
            return ip.substring(0,ip.lastIndexOf("."));
        }else{
            return null;
        }
    }

    //Gets phone IP
    private static String getLocalIP(){
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for(NetworkInterface networkInterface : interfaces){
                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
                for(InetAddress address : addresses){
                    if(!address.isLoopbackAddress()){
                        String sAddr = address.getHostAddress();
                        boolean isIPV4 = !sAddr.contains(":");
                        if(isIPV4){
                            return sAddr;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }


    //Get's laptop/computer IP
    public static String findDeviceIP(String deviceName){

        for(int i = 1;i < 255; i++){
            try{
                String host = subnet + "." + i;
                Log.e(TAG,InetAddress.getByName(host).getHostName());
                if(InetAddress.getByName(host).getHostName().contains(deviceName)){
                    //Ping every IP address in the subnet with the deviceName
                    java.lang.Process process = Runtime.getRuntime().exec("ping -c 1 " + InetAddress.getByName(host).getHostAddress());
                    int returnVal = process.waitFor();
                    if(returnVal == 0){
                        //The ping was received, and sent back. This is the device
                        success = true;
                        //Log.e(TAG,"IP set");
                        return (InetAddress.getByName(host).getHostAddress());
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    protected void onPostExecute(String value){
        localIP = getLocalIP();
    }

    public DeviceSearch(String desktopIP, Context ctx){
        WANTED_DEVICE_IP = desktopIP;
        context = ctx;

    }

    private boolean checkDeviceByIP(){
        try {
            java.lang.Process process = Runtime.getRuntime().exec("ping -c 1 " + InetAddress.getByName(WANTED_DEVICE_IP).getHostAddress());
            int returnVal = 0;
            returnVal = process.waitFor();
            if(returnVal == 0) {
                WANTED_DEVICE_NAME = InetAddress.getByName(WANTED_DEVICE_IP).getHostName();
                return true;
            }else
                return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    protected String doInBackground(Void... strings) {
        //Checking for wifi again because why not
        if(Start.getWifiState()) {
            if(checkDeviceByIP()) {
                ready = true;
                Start.begin(WANTED_DEVICE_IP, getLocalIP());
                //Toast.makeText(context.getApplicationContext(),"Device Available",Toast.LENGTH_SHORT).show();
            }else {
                ready = false;
                //Toast.makeText(context.getApplicationContext(), "No Device Available", Toast.LENGTH_SHORT).show();
            }

        }

        return null;
    }
}
