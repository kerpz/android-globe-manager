package com.ohmnismart.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ohmnismart.db.AccountModel;
import com.ohmnismart.db.Sim;
import com.ohmnismart.db.SimModel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
 
 
public class ActivityMain extends AppCompatActivity {
	
	private static final int RESULT_SETTINGS = 1;

	ViewPager viewPager;
	
	ReceiverUssd receiverUssd;
	ReceiverSms receiverSms;

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
        switch (item.getItemId()) {
        case R.id.action_status:
        	// Check GoSAKTO status @ SMS
			code = "*143*1*7" + Uri.encode("#");
			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
        	startActivityForResult(i, RESULT_SETTINGS);
			return true;
        case R.id.action_balance:
        	// Check your balance @ USSD
			code = "*143*2*1*1" + Uri.encode("#");
        	// Check other's balance @ USSD
			//String code = "*143*2*1*2*" + "9XXXXXXXXX" + Uri.encode("#");
			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
        	startActivityForResult(i, RESULT_SETTINGS);
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

	@Override
	public void onResume() {
	    super.onResume();
		registerReceiver(receiverUssd, new IntentFilter("com.ohmnismart.ussd.action.REFRESH"));
		registerReceiver(receiverSms, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
	}
	
	/*
	@Override
	public void onPause() {
		unregisterReceiver(receiverUssd);
		unregisterReceiver(receiverSms);
	    super.onDestroy();              
	}
	*/

	public class ReceiverUssd extends BroadcastReceiver {
        //private String TAG = ServiceUSSD.class.getSimpleName();
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("text");
            //Log.i(TAG, "Got text: " + text);

			//String test = "[The balance as of 2017/08/02 05:07:30 is P27.0 valid till 2017-08-23 10:02:55 with 0.0 Free texts. Please note that system time may vary from the time on your phone., OK]";
			Matcher matcher = Pattern.compile("balance as of ([0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}) is P([0-9]{1,5}.[0-9]{1,2}) valid till ([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})").matcher(text);
			if (matcher.find()) {
		        //String date = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());

		        //String queryDate = matcher.group(1);
				//queryDate.replace("/", "-");
				String balance = matcher.group(2);
				String balance_expire = matcher.group(3);

				AccountModel db = new AccountModel(context);
				db.readSync();
				db.setBalance(balance);
				db.setBalanceExpire(balance_expire);
				db.writeSync();

				//performGlobalAction(GLOBAL_ACTION_BACK);
				FragmentStatus fragmentStatus = (FragmentStatus) getSupportFragmentManager()
						.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
				if (fragmentStatus != null) {
					fragmentStatus.updateView();
				}
			}

			//String test = "[The balance of 9XXXXXXXXX as of 2017/08/02 05:07:30 is P27.0 valid till 2017-08-23 10:02:55 with 0.0 Free texts.]";
			Matcher matcher2 = Pattern.compile("([0-9]{10}) as of ([0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}) is P([0-9]{1,5}.[0-9]{1,2}) valid till ([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})").matcher(text);
			if (matcher2.find()) {
		        //String date = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());

		        String number = matcher2.group(1);
		        //String queryDate = matcher2.group(2);
				//queryDate.replace("/", "-");
				String balance = matcher2.group(3);
				String balance_expire = matcher2.group(4);

				SimModel db = new SimModel(context);
				Sim sim = new Sim();
				sim.setNumber(number);
				sim.setBalance(balance);
				sim.setBalanceExpire(balance_expire);
				db.updateSim(sim);

				//performGlobalAction(GLOBAL_ACTION_BACK);
				FragmentListSim fragmentListSim = (FragmentListSim) getSupportFragmentManager()
						.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
				if (fragmentListSim != null) {
					fragmentListSim.updateView();
				}
			}
        }
    }

	public class ReceiverSms extends BroadcastReceiver {
		final SmsManager sms = SmsManager.getDefault();

        @Override
		public void onReceive(Context context, Intent intent) {
			final Bundle bundle = intent.getExtras();
			
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				final SmsMessage[] messages = new SmsMessage[pdus.length];

				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				}

				StringBuffer content = new StringBuffer();
				if (messages.length > 0) {
					for (int i = 0; i < messages.length; i++) {
						content.append(messages[i].getMessageBody());
					}
					String sender = messages[0].getDisplayOriginatingAddress();
					//String date = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(messages[0].getTimestampMillis());

					if (sender.equals("8080")) {
						//String test = "Status: Your Unlimited Texts to All Networks from your GoSAKTO subscription will expire on 2017-08-07 22:10:00.,Your remaining 2947MB of consumable internet from your GoSAKTO subscription will expire on 2017-08-07 22:10:00.";
						Matcher matcher = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2}).,Your remaining ([0-9]{1,5})").matcher(content.toString());
						if (matcher.find()) {
							String month = matcher.group(2);
							String day = matcher.group(3);
							String year = matcher.group(1);
							String hour = matcher.group(4);
							String minute = matcher.group(5);
							String second = matcher.group(6);
							String data = matcher.group(7);

							AccountModel db = new AccountModel(context);
							db.readSync();
							db.setData(data);
							db.setDataExpire(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
							db.writeSync();
						}
					}

					if (sender.equals("Globe")) {
						//String test = "Hi! Your load balance as of 08/02/2017 03:02 PM is 27.00, valid till 08/23/2017 10:02 PM. You have 0 texts to all networks. Thanks!"
						Matcher matcher = Pattern.compile("([0-9]{1,5}.[0-9]{1,2}), valid till ([0-9]{2})/([0-9]{2})/([0-9]{4}) ([0-9]{2}):([0-9]{2}) ([A-Z]{2})").matcher(content.toString());
						if (matcher.find()) {
							String balance = matcher.group(1);
							String month = matcher.group(2);
							String day = matcher.group(3);
							String year = matcher.group(4);
							String hour = matcher.group(5);
							String minute = matcher.group(6);
							String ampm = matcher.group(7);

							if (ampm.equals("PM")) {
								hour = Integer.toString(Integer.parseInt(hour) + 12);
							}

							AccountModel db = new AccountModel(context);
							db.readSync();
							db.setBalance(balance);
							db.setBalanceExpire(year+"-"+month+"-"+day+" "+hour+":"+minute+":00");
							db.writeSync();
						}
					}
					FragmentStatus fragmentStatus = (FragmentStatus) getSupportFragmentManager()
							.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
					if (fragmentStatus != null) {
						fragmentStatus.updateView();
					}
				}
			}
		}    
	}

}