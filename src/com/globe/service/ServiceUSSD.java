package com.globe.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.globe.db.AccountModel;
import com.globe.db.Sim;
import com.globe.db.SimModel;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityEvent;

public class ServiceUSSD extends AccessibilityService {
	//public static String TAG = ServiceUSSD.class.getSimpleName();

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		//Log.d(TAG, "onAccessibilityEvent");
		if (event.getClassName().equals("android.app.AlertDialog")) {
			String text = event.getText().toString();
			Matcher matcher;

			//String test = "[The balance as of 2017/08/02 05:07:30 is P27.0 valid till 2017-08-23 10:02:55 with 0.0 Free texts. Please note that system time may vary from the time on your phone., OK]";
			matcher = Pattern.compile("balance as of ([0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}) is P([0-9]{1,5}.[0-9]{1,2}) valid till ([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})").matcher(text);
			if (matcher.find()) {
		        //String date = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());

		        //String queryDate = matcher.group(1);
				//queryDate.replace("/", "-");
				String balance = matcher.group(2);
				String balance_expire = matcher.group(3);

				AccountModel db = new AccountModel(this);
				db.readSync();
				db.setBalance(balance);
				db.setBalanceExpire(balance_expire);
				db.writeSync();
				db.close();

				//performGlobalAction(GLOBAL_ACTION_BACK);
				Intent intent = new Intent("com.globe.status.action.REFRESH");
	            //intent.putExtra("text", text);
				sendBroadcast(intent);
			}

			//String test = "[The balance of 9XXXXXXXXX as of 2017-08-02 05:07:30 is P27.0 valid till 2017-08-23 10:02:55 with 0.0 Free texts.]";
			matcher = Pattern.compile("([0-9]{10}) as of ([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}) is P([0-9]{1,5}.[0-9]{1,2}) valid till ([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})").matcher(text);
			if (matcher.find()) {
		        //String date = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());

				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
				String account_number = sharedPrefs.getString("pref_account_number", "9170000000");

				String number = matcher.group(1);
		        //String queryDate = matcher2.group(2);
				//queryDate.replace("/", "-");
				String balance = matcher.group(3);
				String balance_expire = matcher.group(4);


				if (account_number.equals(number)) {
					AccountModel db = new AccountModel(this);
					db.readSync();
					db.setBalance(balance);
					db.setBalanceExpire(balance_expire);
					db.writeSync();
					db.close();

					//performGlobalAction(GLOBAL_ACTION_BACK);
					Intent intent = new Intent("com.globe.status.action.REFRESH");
		            //intent.putExtra("text", text);
					sendBroadcast(intent);
				}
				else {
					SimModel db = new SimModel(this);
					Sim sim = new Sim();
					sim = db.getSim(number);

					sim.setNumber(number);
					sim.setBalance(balance);
					sim.setBalanceExpire(balance_expire);
					db.updateSim(sim);
					db.close();

					//performGlobalAction(GLOBAL_ACTION_BACK);
					Intent intent = new Intent("com.globe.sim.action.REFRESH");
		            //intent.putExtra("text", text);
					sendBroadcast(intent);
				}
			}
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
}