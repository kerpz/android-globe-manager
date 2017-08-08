package com.ohmnismart.ui;

import com.ohmnismart.db.Sim;
import com.ohmnismart.db.SimModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
 
public class FragmentListSim extends Fragment implements OnItemClickListener, OnItemLongClickListener {
 
	public static final String ARG_ITEM_ID = "list_db";

	Activity activity;
	ListView simListView;
	ArrayList<Sim> sims;

	ListAdapterSim simListAdapter;
	Sim sim;
	SimModel db;

	private GetDevTask task;
	private SwipeRefreshLayout swipeLayout;
	private ProgressDialog progressDialog;
	private FloatingActionButton fab;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		//deviceDAO = new DeviceDAO(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, container, false);
		findViewsById(view);

		//task = new GetDevTask(activity);
		//task.execute((Void) null);
		//updateView();
		
		simListView.setOnItemClickListener(this);
		simListView.setOnItemLongClickListener(this);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    			//Bundle arguments = new Bundle();
    			DialogFragmentAddSim customDevDialogFragment = new DialogFragmentAddSim();
    			//customDevDialogFragment.setArguments(arguments);
    			customDevDialogFragment.show(getFragmentManager(), DialogFragmentAddSim.ARG_ITEM_ID);
            }
        });

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
		simListView = (ListView) view.findViewById(R.id.listViewDevices);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			progressDialog = ProgressDialog.show(activity, "", "Scanning Devices via DB ...", true);
			progressDialog.setCancelable(true);
			updateView();
		}
	}
   
	@Override
	public void onItemClick(AdapterView<?> list, View arg1, int position, long arg3) {
		sim = (Sim) list.getItemAtPosition(position);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> list, View view, int position, long arg3) {
		sim = (Sim) list.getItemAtPosition(position);
		if (sim != null) {
			//findViewsById(view);
			//list.showContextMenuForChild(deviceListView);
			list.showContextMenu();
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	    //menu.setHeaderTitle("Choose what to do");
	    menu.add(0, 0, 0, "Edit");
	    menu.add(0, 1 ,1, "Delete");
	    menu.add(0, 2 ,2, "Get Status");
	    menu.add(0, 3 ,3, "Send Load");
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
	    switch (menuItem.getItemId()) {
	        case 0:
				Bundle arguments = new Bundle();
				arguments.putParcelable("selectedDevice", sim);
				DialogFragmentEditSim customDevDialogFragment = new DialogFragmentEditSim();
				customDevDialogFragment.setArguments(arguments);
				customDevDialogFragment.show(getFragmentManager(), DialogFragmentEditSim.ARG_ITEM_ID);
	            break;
	        case 1:
				db.deleteSim(sim);
				simListAdapter.remove(sim);
	            break;
	    }
	    return true;
	}
	
	public class GetDevTask extends AsyncTask<Void, Void, ArrayList<Sim>> {

		private final WeakReference<Activity> activityWeakRef;

		public GetDevTask(Activity context) {
			this.activityWeakRef = new WeakReference<Activity>(context);
		}

		@Override
		protected ArrayList<Sim> doInBackground(Void... arg0) {
			db = new SimModel(activity.getBaseContext());
			ArrayList<Sim> simList = db.getAllSim();
			return simList;
		}

		@Override
		protected void onPostExecute(ArrayList<Sim> simList) {
			if (activityWeakRef.get() != null && !activityWeakRef.get().isFinishing()) {
				//Log.d("devices", devList.toString());
				sims = simList;
				if (simList != null) {
					if (simList.size() != 0) {
						simListAdapter = new ListAdapterSim(activity, simList);
						simListView.setAdapter(simListAdapter);
						registerForContextMenu(simListView);
					} else {
						Toast.makeText(activity, "No Device Found @ DB", Toast.LENGTH_SHORT).show();
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
		task = new GetDevTask(activity);
		task.execute((Void) null);
	}
}