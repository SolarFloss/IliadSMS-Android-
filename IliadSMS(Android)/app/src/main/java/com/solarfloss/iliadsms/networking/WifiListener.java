package com.solarfloss.iliadsms.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.solarfloss.iliadsms.activities.Start;

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
                DeviceSearch.setReady(false);
                Start.setWifiState(false);
            }else {
                //There is a wifi network connection
                if(!Start.getDebounce()) {
                    Start.setDebounce(true);
                    Start.setWifiState(true);
                    //Start.begin();
                }
            }
        }else{
            //Wifi isn't turned on, so there is no connection
                Log.e(TAG,"Begin Closing");
                DeviceSearch.setReady(false);
                Start.setWifiState(false);
                IncomingDataHandler.closeAll(true);
        }


    }
}
