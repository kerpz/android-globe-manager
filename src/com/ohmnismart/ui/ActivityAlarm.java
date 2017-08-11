package com.ohmnismart.ui;

import com.ohmnismart.db.AccountModel;

import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

// reference https://blog.mikesir87.io/2013/04/android-creating-an-alarm-with-alarmmanager/

public class ActivityAlarm extends AppCompatActivity {
	Ringtone ringtone;
	//Vibrator vibrator;
	Button bIgnore;
	Button bLaunch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alarm);

        // modify to Ignore, Renew, or goto App?
        bIgnore = (Button) findViewById(R.id.bIgnore);
        bLaunch = (Button) findViewById(R.id.bLaunch);

        bIgnore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	ringtone.stop();
            	//vibrator.cancel();
                finish();
            }
        });
        
        bLaunch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	ringtone.stop();
            	//vibrator.cancel();
                finish();
                
            	//Intent intent = new Intent(this, ActivityMain.class);
            	//startActivity(intent);
            }
        });

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        //if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            ringtone.setAudioAttributes(audioAttributes);
        //} else {
        //    ringtone.setStreamType(AudioManager.STREAM_ALARM);
        //}
        ringtone.play();

        //vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //long pattern[] = { 50, 100, 100, 100, 500 };
        //vibrator.vibrate(pattern, 0);

		AccountModel db = new AccountModel(this);
        db.readSync();
        db.setAutoRegisterEnable(false);
    	db.writeSync();
    }

}
