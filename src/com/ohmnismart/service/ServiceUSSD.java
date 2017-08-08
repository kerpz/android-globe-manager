package com.ohmnismart.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ohmnismart.db.AccountModel;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

public class ServiceUSSD extends AccessibilityService {
	//public static String TAG = USSDService.class.getSimpleName();

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		//Log.d(TAG, "onAccessibilityEvent");
		Context context = getBaseContext();
		if (event.getClassName().equals("android.app.AlertDialog")) {
			String text = event.getText().toString();
			//Log.d(TAG, text);

			// add condition if found in string execute this
			//performGlobalAction(GLOBAL_ACTION_BACK);
			
			//Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();

			//String test = "[The balance as of 2017/08/02 05:07:30 is P27.0 valid till 2017-08-23 10:02:55 with 0.0 Free texts. Please note that system time may vary from the time on your phone., OK]";
			//String test = "[The balance of 9XXXXXXXXX as of 2017/08/02 05:07:30 is P27.0 valid till 2017-08-23 10:02:55 with 0.0 Free texts.]";
			Matcher matcher = Pattern.compile("P([0-9]{1,5}.[0-9]{1,2}) valid till ([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})").matcher(text);
			if (matcher.find()) {
		        //String date = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());

		        String balance = matcher.group(1);

				String year = matcher.group(2);
				String month = matcher.group(3);
				String day = matcher.group(4);

				String hour = matcher.group(5);
				String minute = matcher.group(6);
				String second = matcher.group(7);

				//SMSHelper db = new SMSHelper(getBaseContext());
				////db.addSMS(new SMS("date", "ussd", text));
				//db.addSMS(new SMS(date, "USSD", month+"/"+day+"/"+year+" "+hour+":"+minute+":"+second+" "+balance));
				AccountModel db = new AccountModel(context);
				db.readSync();
				db.setBalance(balance);
				db.setBalanceExpire(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
				db.writeSync();

				//performGlobalAction(GLOBAL_ACTION_BACK);
			}

			//Intent intent = new Intent("com.times.ussd.action.REFRESH");
            //intent.putExtra("message", text);
       // write a broad cast receiver and call sendbroadcast() from here, if you want to parse the message for balance, date
            /*
            String tittle="USSD Notification";
            String subject="USSD Started";
            String body="Calling *143#";

            NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify=new Notification.Builder
               (getApplicationContext()).setContentTitle(tittle).setContentText(body).
               setContentTitle(subject).setSmallIcon(R.drawable.ic_launcher).build();
                
               notify.flags |= Notification.FLAG_AUTO_CANCEL;
               notif.notify(0, notify);
           */
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