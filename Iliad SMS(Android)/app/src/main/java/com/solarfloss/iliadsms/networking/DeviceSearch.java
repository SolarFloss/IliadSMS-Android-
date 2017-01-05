package com.solarfloss.iliadsms.networking;

import android.os.AsyncTask;

import com.solarfloss.iliadsms.MainActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Nicholas on 12/31/2016.
 */

public class DeviceSearch extends AsyncTask<String, Void, String>{
    private final String TAG = "DeviceSearch";
    private final String WANTED_DEVICE = "Nicks_PC";
    private static String WANTED_DEVICE_IP = null;
    private String subnet = "192.168.1";

    public static String getWantedDeviceIp() {
        return WANTED_DEVICE_IP;
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
            WANTED_DEVICE_IP = findDeviceIP(WANTED_DEVICE);

        return null;
    }
}
