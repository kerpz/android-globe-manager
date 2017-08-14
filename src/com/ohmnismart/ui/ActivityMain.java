package com.ohmnismart.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
 
 
public class ActivityMain extends AppCompatActivity {
	
	private static final int RESULT_SETTINGS = 1;

	ViewPager viewPager;
	
	ReceiverStatusUpdate receiverStatusUpdate;
	ReceiverSimUpdate receiverSimUpdate;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        /*
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        */
        
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Status"));
        tabLayout.addTab(tabLayout.newTab().setText("Load"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
 
        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
        
        
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
 
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
 
            }
 
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
 
            }
        });
    	receiverStatusUpdate = new ReceiverStatusUpdate();
		registerReceiver(receiverStatusUpdate, new IntentFilter("com.ohmnismart.status.action.REFRESH"));
    	receiverSimUpdate = new ReceiverSimUpdate();
		registerReceiver(receiverSimUpdate, new IntentFilter("com.ohmnismart.sim.action.REFRESH"));
    }
 
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i;
    	String code;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //String videoRtspUrl = sharedPrefs.getString("prefHost", "rtsp://kerpz.no-ip.org/ch1.h264");
    	int method = Integer.valueOf(sharedPrefs.getString("pref_query_method", "1"));
        switch (item.getItemId()) {
        case R.id.action_load_balance:
        	if (method == 1) { // ussd
	        	code = sharedPrefs.getString("pref_ussd_load_balance", "*143*2*1*1#");
	        	code = code.replace("#", "") + Uri.encode("#");
	        	// Check your balance @ USSD
				//code = "*143*2*1*1" + Uri.encode("#");
	        	// Check other's balance @ USSD
				//String code = "*143*2*1*2*" + "9XXXXXXXXX" + Uri.encode("#");
				i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
	        	startActivityForResult(i, RESULT_SETTINGS);
        	}
        	else if (method == 2) { // sms
        		// 222:Bal
	        	code = sharedPrefs.getString("pref_sms_load_balance", "222:Bal");
	        	String[] sms = code.split(":");
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(sms[0], null, sms[1], null, null);
        	}
			return true;
        case R.id.action_point_balance:
        	if (method == 1) { // ussd
            	code = sharedPrefs.getString("pref_ussd_point_balance", "*143*11*1*1#");
            	code = code.replace("#", "") + Uri.encode("#");
            	// Check your point @ USSD
    			//code = "*143*11*1*1" + Uri.encode("#");
    			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
            	startActivityForResult(i, RESULT_SETTINGS);
        	}
        	else if (method == 2) { // sms
        	}
			return true;
        case R.id.action_status:
        	if (method == 1) { // ussd
            	code = sharedPrefs.getString("pref_ussd_status", "*143*1*7#");
            	code = code.replace("#", "") + Uri.encode("#");
            	// Check GoSAKTO status @ SMS
    			//code = "*143*1*7" + Uri.encode("#");
    			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
            	startActivityForResult(i, RESULT_SETTINGS);
        	}
        	else if (method == 2) { // sms
        		// 8080:Gotscombodd70
	        	code = sharedPrefs.getString("pref_sms_status", "8080:Gosakto Status");
	        	String[] sms = code.split(":");
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(sms[0], null, sms[1], null, null);
        	}
			return true;
        case R.id.action_activate_load:
        	if (method == 1) { // ussd
            	code = sharedPrefs.getString("pref_ussd_activate_load", "*143*1*1*6*1*4*1*5*3*1#");
            	code = code.replace("#", "") + Uri.encode("#");
            	// Activate Gotscombodd70 via load @ USSD
    			//code = "*143*1*1*6*1*4*1*5*3*1" + Uri.encode("#");
    			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
            	startActivityForResult(i, RESULT_SETTINGS);
        	}
        	else if (method == 2) { // sms
        		// 8080:Gotscombodd70
	        	code = sharedPrefs.getString("pref_sms_load_balance", "8080:Gotscombodd70");
	        	String[] sms = code.split(":");
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(sms[0], null, sms[1], null, null);
        	}
			return true;
        case R.id.action_activate_point:
        	if (method == 1) { // ussd
            	code = sharedPrefs.getString("pref_ussd_activate_load", "*143*11*2#");
            	code = code.replace("#", "") + Uri.encode("#");
            	// Activate Gotscombodd70 via point @ USSD
    			//code = "*143*11*2" + Uri.encode("#");
    			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
            	startActivityForResult(i, RESULT_SETTINGS);
        	}
        	else if (method == 2) { // sms
        		// 8080:Gotscombodd70
        	}
			return true;
        case R.id.action_accessibility:
        	i = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        	startActivityForResult(i, RESULT_SETTINGS);
			return true;
        case R.id.action_preferences:
			i = new Intent(this, ActivityPreferences.class);
			startActivityForResult(i, RESULT_SETTINGS);
			return true;
        case R.id.action_reset:
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	/*
    @Override
	public void onResume() {
	    super.onResume();
		registerReceiver(receiverUssd, new IntentFilter("com.ohmnismart.ussd.action.REFRESH"));
		registerReceiver(receiverSms, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
	}
	*/
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiverStatusUpdate);
		unregisterReceiver(receiverSimUpdate);
	    super.onDestroy();              
	}

	public class ReceiverStatusUpdate extends BroadcastReceiver {
        @Override
		public void onReceive(Context context, Intent intent) {
			//final Bundle bundle = intent.getExtras();
            //String text = intent.getStringExtra("text");
			FragmentStatus fragmentStatus = (FragmentStatus) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
			if (fragmentStatus.isVisible()) {
				fragmentStatus.updateView();
			}
		}
	}

	public class ReceiverSimUpdate extends BroadcastReceiver {
        @Override
		public void onReceive(Context context, Intent intent) {
			//final Bundle bundle = intent.getExtras();
            //String text = intent.getStringExtra("text");
			FragmentListSim fragmentListSim = (FragmentListSim) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
			if (fragmentListSim.isVisible()) {
				fragmentListSim.updateView();
			}
		}
	}

}