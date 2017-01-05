package com.solarfloss.iliadsms.networking;

import android.os.AsyncTask;
import android.util.Log;

import com.solarfloss.iliadsms.MainActivity;

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

public class DeviceSearch extends AsyncTask<String, Void, String>{
    private final String TAG = "DeviceSearch";
    private final String WANTED_DEVICE = "Nicks_PC";
    private static String WANTED_DEVICE_IP = null;
    private String subnet = null;

    public static String getWantedDeviceIp() {
        return WANTED_DEVICE_IP;
    }


    private String getSubnet(){
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for(NetworkInterface networkInterface : interfaces){
                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
                for(InetAddress address : addresses){
                    if(!address.isLoopbackAddress()){
                        String sAddr = address.getHostAddress();
                        boolean isIPV4 = !sAddr.contains(":");
                        if(isIPV4){
                            return sAddr.substring(0,sAddr.lastIndexOf("."));
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String findDeviceIP(String deviceName){
        for(int i = 1;i < 255; i++){
            try{
                String host = subnet + "." + i;
                if(InetAddress.getByName(host).getHostName().contains(deviceName)){
                    //Ping every IP address in the subnet
                    java.lang.Process process = Runtime.getRuntime().exec("ping -c 1 " + InetAddress.getByName(host).getHostAddress());
                    int returnVal = process.waitFor();
                    if(returnVal == 0){
                        //The ping was received, and sent back. This is the device
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

    @Override
    protected String doInBackground(String... strings) {
        //Checking for wifi again because why not
        if(MainActivity.isWifiConnection())
            subnet = getSubnet();
            WANTED_DEVICE_IP = findDeviceIP(WANTED_DEVICE);

        return null;
    }
}
