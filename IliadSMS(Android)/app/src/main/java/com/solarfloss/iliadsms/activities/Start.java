package com.solarfloss.iliadsms.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.solarfloss.iliadsms.R;
import com.solarfloss.iliadsms.SMSListener;
import com.solarfloss.iliadsms.networking.Authenticate;
import com.solarfloss.iliadsms.networking.DeviceSearch;
import com.solarfloss.iliadsms.networking.IncomingDataHandler;

import org.w3c.dom.Text;

public class Start extends AppCompatActivity {

    public Button btnSearch;
    public TextView txtIP;
    private static String desktopIP;
    private static boolean isWifiConnection = false;
    private static boolean connected = false;
    private static final int callback = 0;
    private static String TAG = "Start";
    private static final Uri SMS_STATUS_URI = Uri.parse("content://sms");
    private static boolean debounce = false;
    public static void setDebounce(boolean value){
        debounce = value;
    }
    public static boolean getDebounce(){
        return debounce;
    }
    public static boolean getWifiState(){return isWifiConnection;}
    public static void setWifiState(boolean value){isWifiConnection = value;}
    private TelephonyManager telephonyManager;
    private static String phoneNumber;

    public ProgressBar progressBar;
    public TextView connectionStatus;
    public TextView deviceNameText;
    public TextView deviceIPText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        txtIP = (TextView)findViewById(R.id.txtIP);
        isWifiConnection = isWifi();

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        connectionStatus = (TextView)findViewById(R.id.connectionStatus);
        deviceNameText = (TextView)findViewById(R.id.deviceNameText);
        deviceIPText = (TextView)findViewById(R.id.deviceIPText);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
                // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, callback);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS}, callback);
                SMSListener smsListener = new SMSListener(new Handler(),this.getApplicationContext());
                this.getContentResolver().registerContentObserver(SMS_STATUS_URI,true,smsListener);
            }
        }else{
            SMSListener smsListener = new SMSListener(new Handler(),this.getApplicationContext());
            this.getContentResolver().registerContentObserver(SMS_STATUS_URI,true,smsListener);
        }

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = telephonyManager.getLine1Number();
    }


    public static void begin(String IP,String localIP){
        desktopIP = IP;
        new Authenticate("1111",localIP,IP,phoneNumber).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public static void authSuccessful(){
        new IncomingDataHandler(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        connected = true;
    }

    public void setStatus(String name, String IP){
        progressBar.setAlpha(0);
        connectionStatus.setText("Connected");
        deviceNameText.setText(name);
        deviceIPText.setText(IP);

    }




    public void btnClick(View view) {
        //TODO: Only one button for now, but check for which button is pressed later
        if(isWifi()){
            if(!txtIP.getText().toString().equals("") && !connected) {
                //Search for device
                progressBar.setAlpha(0);
                new DeviceSearch(txtIP.getText().toString(),getApplicationContext()).execute();
            }
        }
    }


    private boolean isWifi(){
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo.getNetworkId() == -1)
                //There is no wifi network connection
                return false;
            else
                //There is a wifi network connection
                return true;
        }else{
            return false;
        }
    }
}
