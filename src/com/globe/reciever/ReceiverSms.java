package com.globe.reciever;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.globe.db.AccountModel;

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
					Matcher matcher;
					//Status: Your Unlimited Texts to All Networks from your GoSAKTO subscription will expire on 2017-08-07 22:10:00.,Your remaining 2947MB of consumable internet from your GoSAKTO subscription will expire on 2017-08-07 22:10:00.
					//Status: Your Unlimited Texts to All Networks from your GoSAKTO subscription will expire on 2018-08-29 19:02:13.,Your remaining 1021.00MB of consumable internet from your GoSAKTO subscription will expire on 2018-08-29 19:02:13.
					//Matcher matcher = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2}).,Your remaining ([0-9]{1,5})").matcher(content.toString());
					matcher = Pattern.compile("Your remaining ([0-9]+[.][0-9]+)([A-Z])B of consumable internet from your GoSAKTO subscription will expire on ([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})").matcher(content.toString());
					if (matcher.find()) {
						String data;
						if (matcher.group(2).equals("G")) {
							data = Integer.toString(Integer.parseInt(matcher.group(1)) * 1000);
						}
						else {
							data = matcher.group(1);
						}
						//String data = matcher.group(1);
						String year = matcher.group(3);
						String month = matcher.group(4);
						String day = matcher.group(5);
						String hour = matcher.group(6);
						String minute = matcher.group(7);
						String second = matcher.group(8);

						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setData(data);
						db.setDataExpire(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
						db.writeSync();
						db.close();
					}

					//Your GoSAKTO promo has expired. Enjoy a week of surfing, texts and calls with the new GOSAKTO120! It has unli allnet texts, unli Globe/TM calls and 1GB for surfing, valid for 7 days, all for just P120. To register, text GOSAKTO120 to 8080 or dial *143# and choose GoSAKTO. You may also try GOSAKTO140, which has unli allnet texts, unli Globe/TM calls and 2GB for surfing, valid for 7 days.
					matcher = Pattern.compile("Your GoSAKTO promo has expired.").matcher(content.toString());
					if (matcher.find()) {
						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setData("0.00");
						db.setDataExpire("1970-01-01 00:00:00");
						db.writeSync();
						db.close();
					}

					matcher = Pattern.compile("Sorry, you are not yet registered to Globe Prepaid GoSAKTO.").matcher(content.toString());
					if (matcher.find()) {
						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setData("0.00");
						db.setDataExpire("1970-01-01 00:00:00");
						db.writeSync();
						db.close();
					}

					//You're now registered to GoSURF 15 with 40MB of mobile data valid until 2018-08-22, 08:01:05. For more info, text GOSURF REW INFO to 8080. Maintain P1 load. Regular browsing rate applies once GoSURF is used up. You'll receive a text message on your freebie shortly.
					matcher = Pattern.compile("You're now registered to GoSURF ([0-9]+) with ([0-9]+[.][0-9]+)([A-Z])B of mobile data valid until ([0-9]{4})-([0-9]{2})-([0-9]{2}), ([0-9]{2}):([0-9]{2}):([0-9]{2})").matcher(content.toString());
					if (matcher.find()) {
						String data;
						if (matcher.group(3).equals("G")) {
							data = Integer.toString(Integer.parseInt(matcher.group(2)) * 1000);
						}
						else {
							data = matcher.group(2);
						}
						String year = matcher.group(4);
						String month = matcher.group(5);
						String day = matcher.group(6);
						String hour = matcher.group(7);
						String minute = matcher.group(8);
						String second = matcher.group(9);

						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setReward(data);
						db.setRewardExpire(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
						db.writeSync();
						db.close();
					}
					//You're currently registered to GoSURF with remaining 1024MB of mobile data for surfing. Your GoSURF and freebies are valid until 2018-03-10, 15:19:17. For status of your freebie, text GOSURF<denom> <freebie> REW STATUS (ex. GOSURF50 FB REW STATUS). For more info, text GOSURF REW INFO. To stop your GoSURF and freebie, text GOSURF REW STOP. Send to 8080. Stopping your GoSURF will forfeit unused mobile data and stop your freebies
					matcher = Pattern.compile("You're currently registered to GoSURF with remaining ([0-9]+[.][0-9]+)([A-Z])B of mobile data for surfing. Your GoSURF and freebies are valid until ([0-9]{4})-([0-9]{2})-([0-9]{2}), ([0-9]{2}):([0-9]{2}):([0-9]{2})").matcher(content.toString());
					if (matcher.find()) {
						String data;
						if (matcher.group(2).equals("G")) {
							data = Integer.toString(Integer.parseInt(matcher.group(1)) * 1000);
						}
						else {
							data = matcher.group(1);
						}
						//String data = matcher.group(1);
						String year = matcher.group(3);
						String month = matcher.group(4);
						String day = matcher.group(5);
						String hour = matcher.group(6);
						String minute = matcher.group(7);
						String second = matcher.group(8);

						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setReward(data);
						db.setRewardExpire(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
						db.writeSync();
						db.close();
					}

					matcher = Pattern.compile("Sorry, You're not registered to GoSURF.").matcher(content.toString());
					if (matcher.find()) {
						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setReward("0.00");
						db.setRewardExpire("1970-01-01 00:00:00");
						db.writeSync();
						db.close();
					}
					
					//Your GoSURF promo MBs and freebie have expired. Register again to GoSURF to enjoy consumable mobile internet for surfing and more! For a list of GoSURF promos, text GOSURF MORE. For a list of freebies that come with GoSURF, text GOSURF REW FREE. Send to 8080.
					matcher = Pattern.compile("Your GoSURF promo MBs and freebie have expired.").matcher(content.toString());
					if (matcher.find()) {
						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setReward("0.00");
						db.setRewardExpire("1970-01-01 00:00:00");
						db.writeSync();
						db.close();
					}

					matcher = Pattern.compile("Your SurfAlert is currently switched (\\w+).*").matcher(content.toString());
					if (matcher.find()) {
						boolean status = false;
						if (matcher.group(1).equals("ON")) {
							status = true;
						}
						AccountModel db = new AccountModel(context);
						db.readSync();
						db.setSurfAlertEnable(status);
						db.writeSync();
						db.close();
					}
				}

				if (sender.equals("4438")) {
					Matcher matcher;
					//String test = "Status: Your Unlimited Texts to All Networks from your GoSAKTO subscription will expire on 2017-08-07 22:10:00.,Your remaining 2947MB of consumable internet from your GoSAKTO subscription will expire on 2017-08-07 22:10:00.";
					matcher = Pattern.compile("Your regular points of ([0-9]+[.][0-9]+) point/s will expire on ([0-9]{4})-([0-9]{2})-([0-9]{2})").matcher(content.toString());
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
					Matcher matcher = Pattern.compile("([0-9]+[.][0-9]+), valid till ([0-9]{2})/([0-9]{2})/([0-9]{4}) ([0-9]{2}):([0-9]{2}) ([A-Z]{2})").matcher(content.toString());
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
				Intent myIntent = new Intent("com.globe.status.action.REFRESH");
	            //intent.putExtra("text", text);
				context.sendBroadcast(myIntent);
			}
		}
	}    
}
