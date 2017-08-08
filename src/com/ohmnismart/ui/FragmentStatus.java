package com.ohmnismart.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.ohmnismart.db.AccountModel;
import com.ohmnismart.reciever.ReceiverAlarm;
import com.ohmnismart.reciever.ReceiverBoot;
import com.ohmnismart.ui.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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


		updateView();
		
		/*
		tvData.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
                registerForContextMenu(tvData);
                activity.openContextMenu(tvData);
				return false;
			}
		});
		*/
    	
    	tvDataExpire.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				//Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse("2017-08-03 16:15:00");
            	Calendar calendar = Calendar.getInstance();
                try {
					calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse("2017-08-08 05:22:00"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
	            mYear = calendar.get(Calendar.YEAR);
	            mMonth = calendar.get(Calendar.MONTH);;
	            mDay = calendar.get(Calendar.DAY_OF_MONTH);
	            mHour = calendar.get(Calendar.HOUR);
	            mMinute = calendar.get(Calendar.MINUTE);
	            mSecond = calendar.get(Calendar.SECOND);
	            
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int month, int day) {
                            //txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        	mYear = year;
                        	mMonth = month;
                        	mDay = day;
                        }
                    }, mYear, mMonth, mDay);
	            datePickerDialog.show();
                
                TimePickerDialog mTimePicker = new TimePickerDialog(activity,
                	new TimePickerDialog.OnTimeSetListener() {
	                    @Override
	                    public void onTimeSet(TimePicker view, int hour,
	                    						int minute) {
	                    	//textView.setText( selectedHour + ":" + selectedMinute);
                        	mHour = hour;
                        	mMinute = minute;
	                    }
                }, mHour, mMinute, false);
                //mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                
                tvDataExpire.setText(mMonth+"/"+mDay+"/"+mYear+" "+mHour+":"+mMinute+":00");
				return false;
			}
		});
    	
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
				Intent alarmIntent = new Intent(activity, ReceiverAlarm.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);

				AccountModel db = new AccountModel(activity);
                db.readSync();
                
                if (isChecked) {
                	db.setAutoEnable(true);

                    Toast.makeText(activity, "Alarm On", Toast.LENGTH_SHORT).show();

                	Calendar calendar = Calendar.getInstance();

                    //String string = "2011-03-09T03:02:10.823Z";
                    //String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                    //Date date = new SimpleDateFormat(pattern).parse(string);
                    //calendar.setTime(date);
                    //SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    //String formatted = format1.format(cal.getTime());

                    try {
						calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse("2017-08-07 05:29:00"));
					} catch (ParseException e) {
						e.printStackTrace();
					}
                    
                    //calendar.add(Calendar.DATE, 7); // add 7 days
                    //calendar.add(Calendar.MINUTE, -1); // sub 1 minute
                    
                    calendar.add(Calendar.DATE, 1);
                    
                    /*
                    calendar.set(Calendar.HOUR_OF_DAY, 15);
                    calendar.set(Calendar.MINUTE, 47);
                    calendar.set(Calendar.SECOND, 0);
                    */
                    
    				//String dateString = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis());
                    //Toast.makeText(activity, new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()), Toast.LENGTH_LONG).show();
					
                    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 120000, pendingIntent); // 10 sec interval
					
					ComponentName receiver = new ComponentName(activity, ReceiverBoot.class);
					PackageManager pm = activity.getPackageManager();

					pm.setComponentEnabledSetting(receiver,
					        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					        PackageManager.DONT_KILL_APP);
				} else {
                	db.setAutoEnable(false);

                    alarmManager.cancel(pendingIntent);

                    ComponentName receiver = new ComponentName(activity, ReceiverBoot.class);
                    PackageManager pm = activity.getPackageManager();

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

	public void updateView() {
		//task = new GetDevTask(activity);
		//task.execute((Void) null);
        AccountModel db = new AccountModel(activity);
        db.readSync();
    	
    	toggleButton.setChecked(db.getAutoEnable());

    	tvBalance.setText("P" + db.getBalance());
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
	}
}