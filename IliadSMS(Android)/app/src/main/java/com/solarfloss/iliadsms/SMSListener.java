package com.solarfloss.iliadsms;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import com.solarfloss.iliadsms.activities.Start;
import com.solarfloss.iliadsms.networking.DeviceSearch;
import com.solarfloss.iliadsms.networking.OutgoingDataHandler;

/**
 * Created by Nicholas on 12/31/2016.
 */

public class SMSListener extends ContentObserver{
    private String TAG = "SMSListener";
    private Context context;
    private String phoneNumber = "";
    private String[] info;
    private String previousMessage = "";

    public SMSListener(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }


    public void onChange(boolean selfChange){
        //You can never check too many times
        if(Start.getWifiState() && DeviceSearch.isReady()){
            super.onChange(selfChange);
            //We'll basically be listening for any changes in this file directory on the phone
            Uri SMS = Uri.parse("content://sms/");
            Cursor cur = context.getContentResolver().query(SMS,null,null,null,null);

            if(cur != null){
                //The cursor is pointing to nothing, so move it to the first element
                cur.moveToFirst();

                //Get text info
                String textBody = cur.getString(cur.getColumnIndex("body"));
                String number = cur.getString(cur.getColumnIndex("address"));
                String type = cur.getString(cur .getColumnIndex("type"));
                String person = "";


                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
                Cursor lookup = context.getContentResolver().query(uri,new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);

                if(lookup != null) {
                    if (lookup.getCount() > 0) {
                        lookup.moveToFirst();
                        person = lookup.getString(lookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    } else {
                        person = "Unsaved Number";
                    }
                }


                info = new String[]{type,number,person,textBody};
                cur.close();
                lookup.close();
                //Log.e(TAG,type);
                if(type.equals("1")){
                    //Text message received
                    info[0] = "received";
                    new OutgoingDataHandler(info).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else if(type.equals("2")){
                    //Message sent from phone
                    info[0] = "sent";
                    //info[1] = "user";
                    new OutgoingDataHandler(info).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            }
        }
    }



}
