package com.solarfloss.iliadsms;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.solarfloss.iliadsms.networking.DeviceSearch;
import com.solarfloss.iliadsms.networking.IncomingDataHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final Uri SMS_STATUS_URI = Uri.parse("content://sms");
    private static boolean wifiConnection = false;
    private static final int callback = 0;
    private static boolean debounce = false;

    public static void setDebounce(boolean value){
        debounce = value;
    }

    public static boolean getDebounce(){
        return debounce;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //Determine if there is an wifi connection before we start anything
        wifiConnection = checkForWifiConnection();





        //Ask user for SMS permissions
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            Log.e("Stuff","Permission not yet granted");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
               // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, callback);
            }else{
                //Log.e("Stuff","Here");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS}, callback);
            }
        }else{
            //Permission is granted already. Set up sms listener
            SMSListener smsListener = new SMSListener(new Handler(),this.getApplicationContext());
            this.getContentResolver().registerContentObserver(SMS_STATUS_URI,true,smsListener);

            begin();
        }

    }

    public static void begin(){
        //Making sure we didn't lose an internet connection
        if(wifiConnection){
            Log.e("Main","Starting");


            new DeviceSearch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new IncomingDataHandler(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults){
        switch(requestCode){
            case callback:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    begin();
                }else{
                    //Not really sure how to handle this part lol
                    Log.e("Stuff","failure");
                }
            }

        }
    }





    public static boolean isWifiConnection() {
        return wifiConnection;
    }

    public static void setWifiConnection(boolean wifiConnection) {
        MainActivity.wifiConnection = wifiConnection;
    }

    private boolean checkForWifiConnection(){
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
