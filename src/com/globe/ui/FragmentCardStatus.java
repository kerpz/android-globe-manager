package com.globe.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.globe.db.AccountModel;
import com.globe.reciever.ReceiverBoot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Provides UI for the view with Cards.
 */
public class FragmentCardStatus extends Fragment {
	static Activity activity;
	static ContentAdapter contentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		activity = getActivity();
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        contentAdapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

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

        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public ImageView picture;
        TextView tvTitle;
        TextView tvExpire;
        TextView tvAmount;
        TextView tvUnit;
        //Button button;
        Switch switchAlarm;
        ImageButton ibRefresh;
        ImageButton ibClock;
        ImageButton ibCalendar;
        ImageButton ibDownload;
        
        CompoundButton.OnCheckedChangeListener switchListener;
        
        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card, parent, false));
            //picture = (ImageView) itemView.findViewById(R.id.card_image);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvExpire = (TextView) itemView.findViewById(R.id.tvExpire);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            tvUnit = (TextView) itemView.findViewById(R.id.tvUnit);
            
            
            switchAlarm = (Switch) itemView.findViewById(R.id.switchAlarm);
            //switchAlarm.setOnCheckedChangeListener(switchListener);
            //switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            switchListener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                	Context context = v.getContext();
    				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    				Intent alarmIntent = new Intent(context, ActivityAlarm.class);
    		        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    ComponentName receiver = new ComponentName(context, ReceiverBoot.class);
                    PackageManager pm = context.getPackageManager();

                    AccountModel db = new AccountModel(context);
                    db.readSync();
                    
                    if (isChecked) {
                    	Calendar calendar = Calendar.getInstance();
                        try {
    						calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
    					} catch (ParseException e) {
    						e.printStackTrace();
    					}
                        
                        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                            Toast.makeText(context, "Invalid set date, please set a valid date.", Toast.LENGTH_SHORT).show();
                            switchAlarm.setChecked(false);
                        	return;
                        }

                    	db.setAutoRegisterEnable(true);
    					alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    					
    					pm.setComponentEnabledSetting(receiver,
    					        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
    					        PackageManager.DONT_KILL_APP);

    					Toast.makeText(context, "Alarm enabled", Toast.LENGTH_SHORT).show();
                        //Snackbar.make(v, "Alarm enabled", Snackbar.LENGTH_SHORT).show();
    				} else {
                    	db.setAutoRegisterEnable(false);
                        alarmManager.cancel(pendingIntent);

                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);

                        Toast.makeText(context, "Alarm diasbled", Toast.LENGTH_SHORT).show();
                        //Snackbar.make(v, "Alarm disabled", Snackbar.LENGTH_SHORT).show();
                    }
                    db.writeSync();
                    db.close();
                }
            };
            ibRefresh = (ImageButton) itemView.findViewById(R.id.ibRefresh);
            ibRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	Context context = v.getContext();
                	Intent i;
                	String code;
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                    //String videoRtspUrl = sharedPrefs.getString("prefHost", "rtsp://kerpz.no-ip.org/ch1.h264");
                	int method = Integer.valueOf(sharedPrefs.getString("pref_query_method", "1"));
                    switch (getAdapterPosition()) {
                    	case 0:
                        	if (method == 1) { // ussd
                	        	code = sharedPrefs.getString("pref_ussd_load_balance", "*143*2*1*1#");
                	        	code = code.replace("#", "") + Uri.encode("#");
                	        	// Check your balance @ USSD
                				//code = "*143*2*1*1" + Uri.encode("#");
                	        	// Check other's balance @ USSD
                				//String code = "*143*2*1*2*" + "9XXXXXXXXX" + Uri.encode("#");
                				i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
                				context.startActivity(i);
                        	}
                        	else if (method == 2) { // sms
                        		// 222:Bal
                	        	code = sharedPrefs.getString("pref_sms_load_balance", "222:Bal");
                	        	String[] sms = code.split(":");
                				SmsManager smsManager = SmsManager.getDefault();
                				smsManager.sendTextMessage(sms[0], null, sms[1], null, null);
                        	}
                        	break;
                    	case 1:
                        	if (method == 1) { // ussd
                            	code = sharedPrefs.getString("pref_ussd_point_balance", "*143*11*1*1#");
                            	code = code.replace("#", "") + Uri.encode("#");
                            	// Check your point @ USSD
                    			//code = "*143*11*1*1" + Uri.encode("#");
                    			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
                    			context.startActivity(i);
                        	}
                        	else if (method == 2) { // sms
                        	}
                        	break;
                    	case 2:
                        	if (method == 1) { // ussd
                            	code = sharedPrefs.getString("pref_ussd_data_balance", "*143*1*7#");
                            	code = code.replace("#", "") + Uri.encode("#");
                            	// Check GoSAKTO status @ SMS
                    			//code = "*143*1*7" + Uri.encode("#");
                    			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
                    			context.startActivity(i);
                        	}
                        	else if (method == 2) { // sms
                        		// 8080:Gotscombodd70
                	        	code = sharedPrefs.getString("pref_sms_data_balance", "8080:Gosakto Status");
                	        	String[] sms = code.split(":");
                				SmsManager smsManager = SmsManager.getDefault();
                				smsManager.sendTextMessage(sms[0], null, sms[1], null, null);
                        	}
                        	break;
                    }
                	
                    //Snackbar.make(v, "Refresh is pressed " + getAdapterPosition(), Snackbar.LENGTH_LONG).show();
                }
            });

            ibCalendar = (ImageButton) itemView.findViewById(R.id.ibCalendar);
        	ibCalendar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                	Context context = view.getContext();
        	    	AccountModel db = new AccountModel(context);
        	        db.readSync();
                	final Calendar calendar = Calendar.getInstance();
        	        try {
        				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
        			} catch (ParseException e) {
        				e.printStackTrace();
        			}
        	        db.close();
	                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
	                        new DatePickerDialog.OnDateSetListener() {
	                            @Override
	                            public void onDateSet(DatePicker view, int year, int month, int day) {
	                            	switchAlarm.setChecked(false);
	                                calendar.set(Calendar.YEAR, year);
	                                calendar.set(Calendar.MONTH, month);
	                                calendar.set(Calendar.DAY_OF_MONTH, day);

	                                AccountModel db = new AccountModel(view.getContext());
	                                db.readSync();
	                                db.setAutoRegisterDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));
	                                db.setAutoRegisterEnable(true);
	                                db.writeSync();
	                                db.close();

	                                contentAdapter.notifyDataSetChanged();
	                            }
	                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	    	        datePickerDialog.show();
                }
            });
            ibClock = (ImageButton) itemView.findViewById(R.id.ibClock);
            ibClock.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                	Context context = view.getContext();
        	    	AccountModel db = new AccountModel(context);
        	        db.readSync();
                	final Calendar calendar = Calendar.getInstance();
        	        try {
        				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
        			} catch (ParseException e) {
        				e.printStackTrace();
        			}
        	        db.close();
	                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
	                    	new TimePickerDialog.OnTimeSetListener() {
	    	                    @Override
	    	                    public void onTimeSet(TimePicker view, int hour, int minute) {
	                            	switchAlarm.setChecked(false);
	                                calendar.set(Calendar.HOUR_OF_DAY, hour);
	                                calendar.set(Calendar.MINUTE, minute);
	                                //calendar.set(Calendar.SECOND, 0);

	                                AccountModel db = new AccountModel(view.getContext());
	                                db.readSync();
	                                db.setAutoRegisterDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));
	                                db.setAutoRegisterEnable(true);
	                                db.writeSync();
	                                db.close();

	                                contentAdapter.notifyDataSetChanged();
	    	                    }
	                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
	                timePickerDialog.show();
                }
            });
            ibDownload = (ImageButton) itemView.findViewById(R.id.ibDownload);
            ibDownload.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                	switchAlarm.setChecked(false);
                	Context context = view.getContext();
                	AccountModel db = new AccountModel(context);
                    db.readSync();

                    Calendar calendar = Calendar.getInstance();
			        try {
						calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getDataExpire()));
					} catch (ParseException e) {
						e.printStackTrace();
					}

                    //calendar.add(Calendar.DATE, 7); // add 7 days
                    calendar.add(Calendar.MINUTE, -1); // sub 1 minute
                    
                    //calendar.add(Calendar.DATE, 1);
                    db.setAutoRegisterDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.getTimeInMillis()));
                    db.setAutoRegisterEnable(true);
                    db.writeSync();
                    db.close();

                    contentAdapter.notifyDataSetChanged();

                    Snackbar.make(view, "Alarm is automatically set", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        Context context;

        public ContentAdapter(Context context) {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        	context = parent.getContext();
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        	AccountModel db = new AccountModel(context);
            db.readSync();
        	Calendar calendar = Calendar.getInstance();

            switch (position) {
	        	case 0:
	            	try {
	        			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getBalanceExpire()));
	        		} catch (ParseException e) {
	        			e.printStackTrace();
	        		}
		        	holder.tvTitle.setText("Load Balance");
		            holder.tvExpire.setText("expires " + DateUtils.getRelativeTimeSpanString(calendar.getTimeInMillis(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString());
		            holder.tvAmount.setText(String.format("%.2f", Float.valueOf(db.getBalance())));
		            holder.tvUnit.setText("PHP");
		            break;
	        	case 1:
	            	try {
	        			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getPointExpire()));
	        		} catch (ParseException e) {
	        			e.printStackTrace();
	        		}
		        	holder.tvTitle.setText("Point Balance");
		            holder.tvExpire.setText("expires " + DateUtils.getRelativeTimeSpanString(calendar.getTimeInMillis(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString());
		            holder.tvAmount.setText(String.format("%.2f", Float.valueOf(db.getPoint())));
		            holder.tvUnit.setText("PTS");
		            break;
	        	case 2:
	            	try {
	        			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getDataExpire()));
	        		} catch (ParseException e) {
	        			e.printStackTrace();
	        		}
		        	holder.tvTitle.setText("Data Balance");
		            holder.tvExpire.setText("expires " + DateUtils.getRelativeTimeSpanString(calendar.getTimeInMillis(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString());
		            holder.tvAmount.setText(db.getData());
		            holder.tvUnit.setText("MB");
		            break;
	        	case 3:
	            	try {
	        			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(db.getAutoRegisterDate()));
	        		} catch (ParseException e) {
	        			e.printStackTrace();
	        		}
		        	holder.tvTitle.setText("Alarm Settings");
		            holder.tvExpire.setText("triggers " + DateUtils.getRelativeTimeSpanString(calendar.getTimeInMillis(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString());
		            holder.tvAmount.setText(new SimpleDateFormat("h:mm", Locale.getDefault()).format(calendar.getTimeInMillis()));
		            holder.tvUnit.setText(new SimpleDateFormat("a", Locale.getDefault()).format(calendar.getTimeInMillis()));
		            //holder.switchAlarm.setOnCheckedChangeListener(null);
		        	holder.switchAlarm.setChecked(db.getAutoRegisterEnable());
		            holder.switchAlarm.setOnCheckedChangeListener(holder.switchListener);
	
	            	//holder.tvExpire.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
	            	//holder.tvAmount.setTypeface(null, Typeface.NORMAL);
	            	//holder.tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
	            	holder.switchAlarm.setVisibility(View.VISIBLE);
	            	holder.ibRefresh.setVisibility(View.GONE);
	            	holder.ibCalendar.setVisibility(View.VISIBLE);
	            	holder.ibClock.setVisibility(View.VISIBLE);
	            	holder.ibDownload.setVisibility(View.VISIBLE);
		            break;
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
	
	public void updateView() {
    	contentAdapter.notifyDataSetChanged();
    }
    
}