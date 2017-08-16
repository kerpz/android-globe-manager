package com.globe.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.globe.db.AccountModel;
import com.globe.reciever.ReceiverBoot;
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
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
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
    TextView tvPoint;
    TextView tvPointExpire;
    TextView tvData;
    TextView tvDataExpire;
    TextView tvAutoRegisterDate;
    ProgressBar progressBar;


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
        tvBalanceExpire = (TextView) view.findViewById(R.id.tvBalanceExpire);
        tvPoint = (TextView) view.findViewById(R.id.tvPoint);
        tvPointExpire = (TextView) view.findViewById(R.id.tvPointExpire);
        tvData = (TextView) view.findViewById(R.id.tvData);
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
				//Intent alarmIntent = new Intent(activity, ReceiverAlarm.class);
				//PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);
				Intent alarmIntent = new Intent(activity, ActivityAlarm.class);
		        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

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
                db.close();
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
	    menu.add(0, 2, 2, "Auto Set");
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //String videoRtspUrl = sharedPrefs.getString("prefHost", "rtsp://kerpz.no-ip.org/ch1.h264");
	    if (getUserVisibleHint()) {
	    	final Calendar calendar = Calendar.getInstance();

	    	AccountModel db = new AccountModel(activity);
	        db.readSync();

	        try {
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        
	        db.close();

	        switch (menuItem.getItemId()) {
		        case 0:
	                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
	                        new DatePickerDialog.OnDateSetListener() {
	                            @Override
	                            public void onDateSet(DatePicker view, int year,
	                                                  int month, int day) {
	                            	// update
	                                calendar.set(Calendar.YEAR, year);
	                                calendar.set(Calendar.MONTH, month);
	                                calendar.set(Calendar.DAY_OF_MONTH, day);
	                            	updateAutoRegisterDate(calendar);
	                            }
	                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	    	            datePickerDialog.show();
		            break;
		        case 1:
	                TimePickerDialog mTimePicker = new TimePickerDialog(activity,
	                    	new TimePickerDialog.OnTimeSetListener() {
	    	                    @Override
	    	                    public void onTimeSet(TimePicker view, int hour,
	    	                    						int minute) {
	                            	// update
	                                calendar.set(Calendar.HOUR_OF_DAY, hour);
	                                calendar.set(Calendar.MINUTE, minute);
	                                //calendar.set(Calendar.SECOND, 0);
	                            	updateAutoRegisterDate(calendar);
	    	                    }
	                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
	                    //mTimePicker.setTitle("Select Time");
	                    mTimePicker.show();
		            break;
		        case 2:
			        try {
						calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getDataExpire()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
                    //calendar.add(Calendar.DATE, 7); // add 7 days
                    calendar.add(Calendar.MINUTE, -1); // sub 1 minute
                    
                    //calendar.add(Calendar.DATE, 1);
                	updateAutoRegisterDate(calendar);
		            break;
		    }
		    return true;
	    }
	    return false;
	}

	private void updateAutoRegisterDate(Calendar calendar) {
		tvAutoRegisterDate.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));

		AccountModel db = new AccountModel(activity);
        db.readSync();
        db.setAutoRegisterDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));
        db.setAutoRegisterEnable(true);
        db.writeSync();
        db.close();
    	toggleButton.setChecked(true);

	}

	public void updateView() {
		//task = new GetDevTask(activity);
		//task.execute((Void) null);
        AccountModel db = new AccountModel(activity);
        db.readSync();
    	
        Calendar calendar = Calendar.getInstance();

    	tvBalance.setText("P" + String.format("%.2f", Float.valueOf(db.getBalance())));
    	try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getBalanceExpire()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	tvBalanceExpire.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));

    	tvData.setText(db.getData() + "MB");
        try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getDataExpire()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	tvDataExpire.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));

    	tvPoint.setText(db.getPoint());
        try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getPointExpire()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	tvPointExpire.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));

    	toggleButton.setChecked(db.getAutoRegisterEnable());
    	try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	tvAutoRegisterDate.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));
    	
    	db.close();
    	
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
}