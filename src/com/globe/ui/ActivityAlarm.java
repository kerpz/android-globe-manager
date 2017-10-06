package com.globe.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.globe.db.AccountModel;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

// reference https://blog.mikesir87.io/2013/04/android-creating-an-alarm-with-alarmmanager/

public class ActivityAlarm extends AppCompatActivity {
	//Ringtone ringtone;
	MediaPlayer ringtone;
	Vibrator vibrator;
	NotificationManager notificationManager;
	Button bIgnore;
	Button bLaunch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window win = getWindow();
        win.addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN |
        		WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                //WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );
        
        setContentView(R.layout.activity_alarm);

        // modify to Ignore, Renew, or goto App?
        bIgnore = (Button) findViewById(R.id.bIgnore);
        bLaunch = (Button) findViewById(R.id.bLaunch);

	    //final ComponentName receiver = new ComponentName(this, ReceiverBoot.class);
        //final PackageManager pm = this.getPackageManager();
        bIgnore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	ringtone.stop();
            	vibrator.cancel();
        	    notificationManager.cancel(0);

        	    //pm.setComponentEnabledSetting(receiver,
                //        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                //        PackageManager.DONT_KILL_APP);

                finish();
            }
        });
        
    	final Intent intent = new Intent(this, ActivityMain.class);
        bLaunch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	ringtone.stop();
            	vibrator.cancel();
        	    notificationManager.cancel(0);

                //pm.setComponentEnabledSetting(receiver,
                //        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                //        PackageManager.DONT_KILL_APP);

            	startActivity(intent);

            	finish();
            }
        });

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        //ringtone = RingtoneManager.getRingtone(this, alarmUri);
        /*
        //if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            ringtone.setAudioAttributes(audioAttributes);
        //} else {
        //    ringtone.setStreamType(AudioManager.STREAM_ALARM);
        //}
        */
        //ringtone.play();
        
        //AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
        
        //ringtone = MediaPlayer.create(this, alarmUri);
        try {
            ringtone = new MediaPlayer();
			ringtone.setDataSource(this, alarmUri);
	        ringtone.setLooping(true);
	        //ringtone.setVolume((float) 1.0, (float) 1.0);
	        ringtone.setAudioStreamType(AudioManager.STREAM_ALARM);

	        ringtone.prepare();
	        ringtone.start();        
		} catch (Exception e) {
			e.printStackTrace();
		}

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long pattern[] = { 500, 500 };
        vibrator.vibrate(pattern, 0);

        Calendar calendar = Calendar.getInstance();
        
        //Intent notificationIntent = new Intent(this, ActivityAlarm.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
        //		PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Alarm Tag"
        //);
        //wakeLock.acquire();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
        	.setContentTitle(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()))
        	.setContentText("GoSakto is about to expire.")
        	.setSmallIcon(R.drawable.ic_launcher)
        	//.setContentIntent(pendingIntent)
        	.build();
            
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);

        AccountModel db = new AccountModel(this);
		db.readSync();
		db.setAutoRegisterEnable(false);
		db.writeSync();
		db.close();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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

                finish();
            }
        }, duration * 60000);
    }

}
