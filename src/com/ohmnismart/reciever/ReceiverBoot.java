package com.ohmnismart.reciever;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.ohmnismart.db.AccountModel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            AccountModel db = new AccountModel(context);
            db.readSync();

            if (db.getAutoEnable() == true) {
				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				Intent alarmIntent = new Intent(context, ReceiverAlarm.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                Calendar calendar = Calendar.getInstance();
                
                try {
					calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse("2017-08-07 05:29:00"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
                
                //calendar.add(Calendar.DATE, 7); // add 7 days
                //calendar.add(Calendar.MINUTE, -1); // sub 1 minute
                
                calendar.add(Calendar.DATE, 1);
                
                /*
                calendar.set(Calendar.HOUR_OF_DAY, 15);
                calendar.set(Calendar.MINUTE, 47);
                calendar.set(Calendar.SECOND, 0);
                */
                
				//String dateString = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis());
                //Toast.makeText(ActivityAlarm.this, new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()), Toast.LENGTH_LONG).show();
				
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 120000, pendingIntent); // 10 sec interval
        	}
            //Intent alarmIntent = new Intent(context, ReceiverAlarm.class);
            //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            //AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //int interval = 8000;
            //manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

            //Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
        }
    }

}
