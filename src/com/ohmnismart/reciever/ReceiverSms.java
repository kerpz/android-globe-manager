package com.ohmnismart.reciever;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.ohmnismart.db.AccountModel;

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
						db.close();
					}
				}

				if (sender.equals("4438")) {
					//String test = "Status: Your Unlimited Texts to All Networks from your GoSAKTO subscription will expire on 2017-08-07 22:10:00.,Your remaining 2947MB of consumable internet from your GoSAKTO subscription will expire on 2017-08-07 22:10:00.";
					Matcher matcher = Pattern.compile("([0-9]{1,5}) point/s will expire on ([0-9]{4})-([0-9]{2})-([0-9]{2})").matcher(content.toString());
					if (matcher.find()) {
						String point = matcher.group(1);
						String year = matcher.group(2);
						String month = matcher.group(3);
						String day = matcher.group(4);
						//String hour = matcher.group(5);
						//String minute = matcher.group(6);
						//String second = matcher.group(7);

						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setPoint(point);
						db.setPointExpire(year+"-"+month+"-"+day+" 00:00:00");
						db.writeSync();
						db.close();
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
						db.close();
					}
				}
				Intent myIntent = new Intent("com.ohmnismart.status.action.REFRESH");
	            //intent.putExtra("text", text);
				context.sendBroadcast(myIntent);
			}
		}
	}    
}
