package com.globe.reciever;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.globe.db.AccountModel;
import com.globe.ui.ActivityMain;
import com.globe.ui.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;


public class ReceiverAlarm extends BroadcastReceiver {

	public static MediaPlayer ringtone;
	public static Vibrator vibrator;

	@Override
    public void onReceive(Context context, Intent intent) {
        //if (intent.getAction().equals("android.intent.action.ALARM_CALLED")) {
        	NotificationManager notificationManager;

            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            try {
                ringtone = new MediaPlayer();
    			ringtone.setDataSource(context, alarmUri);
    	        ringtone.setLooping(true);
    	        //ringtone.setVolume((float) 1.0, (float) 1.0);
    	        ringtone.setAudioStreamType(AudioManager.STREAM_ALARM);

    	        ringtone.prepare();
    	        ringtone.start();        
    		} catch (Exception e) {
    			e.printStackTrace();
    		}

            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long pattern[] = { 500, 500 };
            vibrator.vibrate(pattern, 0);

            Calendar calendar = Calendar.getInstance();

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, ActivityMain.class), PendingIntent.FLAG_UPDATE_CURRENT);
            
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification.Builder(context)
            	.setContentTitle(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()))
            	.setContentText("GoSakto is about to expire.")
            	.setSmallIcon(R.drawable.ic_launcher)
            	.setContentIntent(contentIntent)
            	.build();
                
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, notification);

            AccountModel db = new AccountModel(context);
    		db.readSync();
    		db.setAutoRegisterEnable(false);
    		db.writeSync();
    		db.close();

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        	int duration = Integer.valueOf(sharedPrefs.getString("pref_alarm_duration", "3"));

        	Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                	ringtone.stop();
                	vibrator.cancel();
            	    //notificationManager.cancel(0);

                    //pm.setComponentEnabledSetting(receiver,
                    //        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    //        PackageManager.DONT_KILL_APP);

                    //wakeLock.release();

                    //finish();
                }
            }, duration * 60000);
        }
    //}

}
