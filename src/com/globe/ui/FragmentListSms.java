package com.globe.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.globe.db.Sms;
import com.globe.db.SmsModel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class FragmentListSms extends Fragment implements OnItemLongClickListener {

	Activity activity;
	ListView smsListView;
	ArrayList<Sms> smss;

	ListAdapterSms smsListAdapter;
	Sms sms;
	SmsModel db;

	private GetSmsTask task;
	private SwipeRefreshLayout swipeLayout;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		//deviceDAO = new DeviceDAO(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_devices, container, false);
		findViewsById(view);
 
		//deviceListView.setOnItemClickListener(this);
		smsListView.setOnItemLongClickListener(this);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		//swipeLayout.setColorSchemeResources(R.color.flat_button_text);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            	updateView();
            }
        });
    	
    	return view;
	}

	private void findViewsById(View view) {
		smsListView = (ListView) view.findViewById(R.id.listViewDevices);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			progressDialog = ProgressDialog.show(activity, "", "Reading SMS via DB ...", true);
			progressDialog.setCancelable(true);
			updateView();
		}
	}
   
	@Override
	public boolean onItemLongClick(AdapterView<?> list, View view, int position, long arg3) {
		sms = (Sms) list.getItemAtPosition(position);
		if (sms != null) {
			//findViewsById(view);
			//list.showContextMenuForChild(deviceListView);
			list.showContextMenu();
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	    //menu.setHeaderTitle("Choose what to do");
	    //menu.add(0, 0, 0, "Edit");
	    menu.add(0, 0 ,0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
	    switch (menuItem.getItemId()) {
	        case 0:
				db.deleteSMS(sms);
				smsListAdapter.remove(sms);
	            break;
	    }
	    return true;
	}
	
	public class GetSmsTask extends AsyncTask<Void, Void, ArrayList<Sms>> {

		private final WeakReference<Activity> activityWeakRef;

		public GetSmsTask(Activity context) {
			this.activityWeakRef = new WeakReference<Activity>(context);
		}

		@Override
		protected ArrayList<Sms> doInBackground(Void... arg0) {
			db = new SmsModel(activity.getBaseContext());
			ArrayList<Sms> smsList = db.getAllSMS();
			return smsList;
		}

		@Override
		protected void onPostExecute(ArrayList<Sms> smsList) {
			if (activityWeakRef.get() != null && !activityWeakRef.get().isFinishing()) {
				//Log.d("devices", devList.toString());
				smss = smsList;
				if (smsList != null) {
					if (smsList.size() != 0) {
						smsListAdapter = new ListAdapterSms(activity, smsList);
						smsListView.setAdapter(smsListAdapter);
						registerForContextMenu(smsListView);
					} else {
						Toast.makeText(activity, "No SMS Found @ DB", Toast.LENGTH_SHORT).show();
					}
				}
        		if (swipeLayout.isRefreshing()) {
	            	swipeLayout.setRefreshing(false);
	            }
	            else {
	            	progressDialog.dismiss();
	            }

			}
		}
	}

	/*
	 * This method is invoked from MainActivity onFinishDialog() method. It is
	 * called from CustomEmpDialogFragment when an employee record is updated.
	 * This is used for communicating between fragments.
	 */
	public void updateView() {
		task = new GetSmsTask(activity);
		task.execute((Void) null);
	}
}
