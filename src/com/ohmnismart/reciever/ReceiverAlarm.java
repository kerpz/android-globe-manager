package com.ohmnismart.reciever;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.ohmnismart.db.AccountModel;
import com.ohmnismart.ui.ActivityMain;
import com.ohmnismart.ui.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();
        */
    	
    	AccountModel db = new AccountModel(context);
        db.readSync();

        if (Float.valueOf(db.getBalance()) < 80.0) {
            Calendar calendar = Calendar.getInstance();
            
            Intent notificationIntent = new Intent(context, ActivityMain.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationManager notif = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = new Notification.Builder(context)
            	.setContentTitle(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()))
            	.setContentText("Gotscombodd70 is about to expire.")
            	.setSmallIcon(R.drawable.ic_launcher)
            	.setContentIntent(pendingIntent)
            	.build();
                
           notify.flags |= Notification.FLAG_AUTO_CANCEL;
           notif.notify(0, notify);
    	}

    	db.setAutoRegisterEnable(false);
    	db.writeSync();
    }
}