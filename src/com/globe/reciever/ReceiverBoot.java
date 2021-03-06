package com.globe.reciever;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.globe.db.AccountModel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent = new Intent(context, ReceiverAlarm.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			//Intent alarmIntent = new Intent(context, ActivityAlarm.class);
	        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AccountModel db = new AccountModel(context);
            db.readSync();

            Calendar calendar = Calendar.getInstance();
            
            try {
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

            if (db.getAutoRegisterEnable()) {
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 120000, pendingIntent); // 10 sec interval
    			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            
            db.close();
        }
    }

}
