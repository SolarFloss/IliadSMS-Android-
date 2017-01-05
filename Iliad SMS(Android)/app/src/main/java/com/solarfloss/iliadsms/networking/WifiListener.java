package com.solarfloss.iliadsms.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.solarfloss.iliadsms.MainActivity;

public class WifiListener extends BroadcastReceiver {
    private String TAG = "WiFi";
    private static int count = 0;

    public WifiListener() {
    }


    //Listen for changes in wifi(Connected/Disconnected)
    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo.getNetworkId() == -1) {
                //There is no wifi network connection
                //Log.e(TAG,String.valueOf(count++));
                //Log.e(TAG,"No Connection");
                MainActivity.setWifiConnection(false);
            }else {
                //There is a wifi network connection
                if(!MainActivity.getDebounce()) {
                    MainActivity.setDebounce(true);
                    MainActivity.setWifiConnection(true);
                    MainActivity.begin();
                }
            }
        }else{
            //Wifi isn't turned on, so there is no connection
            //if(!IncomingDataHandler.getSwitchDebounce()){
                Log.e(TAG,"Begin Closing");
                MainActivity.setWifiConnection(false);
                IncomingDataHandler.closeAll(true);
            //}
        }


    }
}
