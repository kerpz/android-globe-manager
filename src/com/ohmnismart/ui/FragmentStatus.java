package com.ohmnismart.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ohmnismart.db.AccountModel;
import com.ohmnismart.db.Sim;
import com.ohmnismart.db.SimModel;
import com.ohmnismart.reciever.ReceiverAlarm;
import com.ohmnismart.reciever.ReceiverBoot;
import com.ohmnismart.ui.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
 
public class FragmentStatus extends Fragment {
 
	Activity activity;

	ToggleButton toggleButton;
    TextView tvBalance;
    TextView tvBalanceExpire;
    TextView tvData;
    TextView tvDataExpire;
    TextView tvAutoRegisterDate;
    ProgressBar progressBar;

	int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    int mSecond;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();

		// if we encounter problem here, move this to onResume and unregister at onDestroy
		activity.registerReceiver(new ReceiverUssd(), new IntentFilter("com.ohmnismart.ussd.action.REFRESH"));
		activity.registerReceiver(new ReceiverSms(), new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_status, container, false);
		//findViewsById(view);

		toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);
        tvBalance = (TextView) view.findViewById(R.id.tvBalance);
        tvData = (TextView) view.findViewById(R.id.tvData);
        tvBalanceExpire = (TextView) view.findViewById(R.id.tvBalanceExpire);
        tvDataExpire = (TextView) view.findViewById(R.id.tvDataExpire);
        tvAutoRegisterDate = (TextView) view.findViewById(R.id.tvAutoRegisterDate);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

		updateView();
		
		tvAutoRegisterDate.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
                activity.openContextMenu(tvAutoRegisterDate);
				return true;
			}
		});
        registerForContextMenu(tvAutoRegisterDate);
    	
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
				Intent alarmIntent = new Intent(activity, ReceiverAlarm.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);

                ComponentName receiver = new ComponentName(activity, ReceiverBoot.class);
                PackageManager pm = activity.getPackageManager();

                AccountModel db = new AccountModel(activity);
                db.readSync();
                
                if (isChecked) {

                	Calendar calendar = Calendar.getInstance();

                    //String string = "2011-03-09T03:02:10.823Z";
                    //String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                    //Date date = new SimpleDateFormat(pattern).parse(string);
                    //calendar.setTime(date);
                    //SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    //String formatted = format1.format(cal.getTime());

                    try {
						calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
                    
                    if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                        Toast.makeText(activity, "Invalid set date, please set a valid date.", Toast.LENGTH_SHORT).show();
                    	toggleButton.setChecked(false);
                    	return;
                    }
                    
                    
                	db.setAutoRegisterEnable(true);

                    Toast.makeText(activity, "Alarm On", Toast.LENGTH_SHORT).show();

                    //calendar.add(Calendar.DATE, 7); // add 7 days
                    //calendar.add(Calendar.MINUTE, -1); // sub 1 minute
                    
                    //calendar.add(Calendar.DATE, 1);
                    
                    /*
                    calendar.set(Calendar.HOUR_OF_DAY, 15);
                    calendar.set(Calendar.MINUTE, 47);
                    calendar.set(Calendar.SECOND, 0);
                    */
                    
    				//String dateString = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis());
                    //Toast.makeText(activity, new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()), Toast.LENGTH_LONG).show();
					
                    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
					//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 120000, pendingIntent); // 10 sec interval
					alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
					
					pm.setComponentEnabledSetting(receiver,
					        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					        PackageManager.DONT_KILL_APP);
				} else {
                	db.setAutoRegisterEnable(false);

                    alarmManager.cancel(pendingIntent);

                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);

                    Toast.makeText(activity, "Alarm Off", Toast.LENGTH_SHORT).show();
                }
                db.writeSync();
            }
        });

        int accessibilityEnabled = 0;
        try {
        	accessibilityEnabled = Settings.Secure.getInt(
        			activity.getApplicationContext().getContentResolver(),
	                android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        	e.printStackTrace();
        }
        if (accessibilityEnabled == 0) {
            Toast.makeText(activity, "Please turn on the accessibility option for this application", Toast.LENGTH_LONG).show();
        }

        return view;
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	    //menu.setHeaderTitle("Choose what to do");
	    menu.add(0, 0 ,0, "Edit Date");
	    menu.add(0, 1, 1, "Edit Time");
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //String videoRtspUrl = sharedPrefs.getString("prefHost", "rtsp://kerpz.no-ip.org/ch1.h264");
	    if (getUserVisibleHint()) {
	        // context menu logic
	    	Calendar calendar = Calendar.getInstance();

	    	AccountModel db = new AccountModel(activity);
	        db.readSync();

	        try {
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        mYear = calendar.get(Calendar.YEAR);
	        mMonth = calendar.get(Calendar.MONTH);
	        mDay = calendar.get(Calendar.DAY_OF_MONTH);
	        mHour = calendar.get(Calendar.HOUR_OF_DAY);
	        mMinute = calendar.get(Calendar.MINUTE);
	        mSecond = calendar.get(Calendar.SECOND);

	        switch (menuItem.getItemId()) {
		        case 0:
	                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
	                        new DatePickerDialog.OnDateSetListener() {
	                            @Override
	                            public void onDateSet(DatePicker view, int year,
	                                                  int month, int day) {
	                            	mYear = year;
	                            	mMonth = month;
	                            	mDay = day;

	                            	// update
	                            	updateAutoRegisterDate();
	                            }
	                        }, mYear, mMonth, mDay);
	    	            datePickerDialog.show();
		            break;
		        case 1:
	                TimePickerDialog mTimePicker = new TimePickerDialog(activity,
	                    	new TimePickerDialog.OnTimeSetListener() {
	    	                    @Override
	    	                    public void onTimeSet(TimePicker view, int hour,
	    	                    						int minute) {
	                            	mHour = hour;
	                            	mMinute = minute;
	                            	// update
	                            	updateAutoRegisterDate();
	    	                    }
	                    }, mHour, mMinute, false);
	                    //mTimePicker.setTitle("Select Time");
	                    mTimePicker.show();
		            break;
		    }
		    return true;
	    }
	    return false;
	}

	private void updateAutoRegisterDate() {
        tvAutoRegisterDate.setText(String.format("%02d", mMonth + 1)+"/"+String.format("%02d", mDay)+"/"+String.format("%04d", mYear)
        		+" "+String.format("%02d", mHour)+":"+String.format("%02d", mMinute)+":"+String.format("%02d", mSecond));

    	AccountModel db = new AccountModel(activity);
        db.readSync();
        db.setAutoRegisterDate(String.format("%04d", mYear)+"-"+String.format("%02d", mMonth + 1)+"-"+String.format("%02d", mDay)
        		+" "+String.format("%02d", mHour)+":"+String.format("%02d", mMinute)+":"+String.format("%02d", mSecond));
        db.writeSync();
	}

	public void updateView() {
		//task = new GetDevTask(activity);
		//task.execute((Void) null);
        AccountModel db = new AccountModel(activity);
        db.readSync();
    	
    	toggleButton.setChecked(db.getAutoRegisterEnable());

    	tvBalance.setText("P" + String.format("%.2f", Float.valueOf(db.getBalance())));
    	tvData.setText(db.getData() + "MB");

        Calendar calendar = Calendar.getInstance();

        try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getDataExpire()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	tvDataExpire.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));

    	try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getBalanceExpire()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	tvBalanceExpire.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));
    	
    	try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	tvAutoRegisterDate.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));
    	
    	TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
    	// for example value of first element
    	CellInfo cellInfo = (CellInfo) telephonyManager.getAllCellInfo().get(0);
    	int level = 0;
    	if (cellInfo instanceof CellInfoLte) {
    		CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
    		CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
    		level = cellSignalStrengthLte.getLevel(); // 0 - 4
    	}
    	else if (cellInfo instanceof CellInfoWcdma) {
    		CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
    		CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
    		level = cellSignalStrengthWcdma.getLevel(); // 0 - 4
    	}
    	else if (cellInfo instanceof CellInfoCdma) {
    		CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
    		CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
    		level = cellSignalStrengthCdma.getLevel(); // 0 - 4
    	}
    	else if (cellInfo instanceof CellInfoGsm) {
    		CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
    		CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
    		level = cellSignalStrengthGsm.getLevel(); // 0 - 4
    	}
    	 
    	progressBar.setProgress(level * 25);
	}

	/*
	@Override
	public void onResume() {
	    super.onResume();
	    registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_GET_CONTENT));
	}
	
	@Override
	public void onDestroy() {
	    unregisterReceiver(broadcastReceiver);      
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
			}

			updateView();
        }
    }

	public class ReceiverSms extends BroadcastReceiver {
		final SmsManager sms = SmsManager.getDefault();

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
				}
			}

			updateView();
		}    
	}

}