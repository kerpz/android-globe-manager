package com.ohmnismart.ui;

import java.util.List;

import com.ohmnismart.ui.R;
import com.ohmnismart.db.Sim;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapterSim extends ArrayAdapter<Sim> {

	private Context context;
	List<Sim> sims;

	public ListAdapterSim(Context context, List<Sim> sims) {
		super(context, R.layout.list_row, sims);
		this.context = context;
		this.sims = sims;
	}

	private class ViewHolder {
		TextView devHostNameTxt;
		TextView devMACTxt;
		TextView devIPTxt;
	}

	@Override
	public int getCount() {
		return sims.size();
	}

	@Override
	public Sim getItem(int position) {
		return sims.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_row, parent, false);
			holder = new ViewHolder();

			holder.devHostNameTxt = (TextView) convertView
					.findViewById(R.id.textView1);
			holder.devMACTxt = (TextView) convertView
					.findViewById(R.id.textView2);
			holder.devIPTxt = (TextView) convertView
					.findViewById(R.id.textView3);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Sim sim = (Sim) getItem(position);

		holder.devHostNameTxt.setText(sim.getNumber());
		holder.devMACTxt.setText(sim.getBalanceExpire());
		//holder.devIPTxt.setText("P"+sim.getBalance());
		holder.devIPTxt.setText("P"+String.format("%.2f", Float.valueOf(sim.getBalance())));
		
		

		return convertView;
	}

	@Override
	public void add(Sim sim) {
		sims.add(sim);
		notifyDataSetChanged();
		super.add(sim);
	}

	@Override
	public void remove(Sim sim) {
		sims.remove(sim);
		notifyDataSetChanged();
		super.remove(sim);
	}
}