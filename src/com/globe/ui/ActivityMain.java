package com.globe.ui;

import java.util.ArrayList;
import java.util.List;

import com.globe.reciever.ReceiverAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
 
 
public class ActivityMain extends AppCompatActivity {
	
	private static final int RESULT_SETTINGS = 1;

	//ViewPager viewPager;
	
	ReceiverStatusUpdate receiverStatusUpdate;
	ReceiverSimUpdate receiverSimUpdate;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        // Show menu icon
        /*
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        */
        
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //tabLayout.addTab(tabLayout.newTab().setText("Status"));
        //tabLayout.addTab(tabLayout.newTab().setText("Load"));
 
        /*
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
        */
    	receiverStatusUpdate = new ReceiverStatusUpdate();
		registerReceiver(receiverStatusUpdate, new IntentFilter("com.globe.status.action.REFRESH"));
    	receiverSimUpdate = new ReceiverSimUpdate();
		registerReceiver(receiverSimUpdate, new IntentFilter("com.globe.sim.action.REFRESH"));
		
		if (ReceiverAlarm.ringtone != null) ReceiverAlarm.ringtone.stop();
		if (ReceiverAlarm.vibrator != null) ReceiverAlarm.vibrator.cancel();
		
    }
 
    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentCardStatus(), "Status");
        adapter.addFragment(new FragmentListSim(), "Sim");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	//int method = Integer.valueOf(sharedPrefs.getString("pref_query_method", "1"));

    	SmsManager smsManager = SmsManager.getDefault();
    	String[] code;
        switch (item.getItemId()) {
	        case R.id.action_activate_load1:
	        	code = sharedPrefs.getString("pref_sms_activate_load1", "8080:Gotscombodd70").split(":");
				smsManager.sendTextMessage(code[0], null, code[1], null, null);
				return true;
	        case R.id.action_activate_load2:
	        	code = sharedPrefs.getString("pref_sms_activate_load2", "8080:Gotscombogbbff108").split(":");
				smsManager.sendTextMessage(code[0], null, code[1], null, null);
				return true;
	        case R.id.action_activate_point1:
	        	// redeem gosurf50 (redeem item)
	        	// gift gosurf50 917xxxxxxx (gift item)
	        	// share 917xxxxxxx 10 (share points)
	        	code = sharedPrefs.getString("pref_sms_activate_point1", "4438:Redeem gosurf50").split(":");
				smsManager.sendTextMessage(code[0], null, code[1], null, null);
				return true;
	        case R.id.action_activate_point2:
	        	// redeem gosurf50 (redeem item)
	        	// gift gosurf50 917xxxxxxx (gift item)
	        	// share 917xxxxxxx 10 (share points)
	        	code = sharedPrefs.getString("pref_sms_activate_point2", "4438:Redeem gosurf99").split(":");
				smsManager.sendTextMessage(code[0], null, code[1], null, null);
				return true;
	        case R.id.action_accessibility:
	        	i = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
	        	startActivityForResult(i, RESULT_SETTINGS);
				return true;
	        case R.id.action_preferences:
				i = new Intent(this, ActivityPreferences.class);
				startActivityForResult(i, RESULT_SETTINGS);
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
        	FragmentCardStatus fragmentCardStatus = (FragmentCardStatus) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.viewPager + ":0");
			if (fragmentCardStatus.isVisible()) {
				fragmentCardStatus.updateView();
			}
		}
	}

	public class ReceiverSimUpdate extends BroadcastReceiver {
        @Override
		public void onReceive(Context context, Intent intent) {
			//final Bundle bundle = intent.getExtras();
            //String text = intent.getStringExtra("text");
			FragmentListSim fragmentListSim = (FragmentListSim) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.viewPager + ":1");
			if (fragmentListSim.isVisible()) {
				fragmentListSim.updateView();
			}
		}
	}

}