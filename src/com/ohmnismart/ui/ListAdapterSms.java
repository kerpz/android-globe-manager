package com.ohmnismart.ui;

import java.util.List;

import com.ohmnismart.ui.R;
import com.ohmnismart.db.Sms;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapterSms extends ArrayAdapter<Sms> {

	private Context context;
	List<Sms> smss;

	public ListAdapterSms(Context context, List<Sms> smss) {
		super(context, R.layout.list_row, smss);
		this.context = context;
		this.smss = smss;
	}

	private class ViewHolder {
		TextView devHostNameTxt;
		TextView devMACTxt;
		TextView devIPTxt;
	}

	@Override
	public int getCount() {
		return smss.size();
	}

	@Override
	public Sms getItem(int position) {
		return smss.get(position);
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
		Sms sms = (Sms) getItem(position);

		holder.devHostNameTxt.setText(sms.getDate());
		holder.devMACTxt.setText(sms.getSender());
		holder.devIPTxt.setText(sms.getContent());

		return convertView;
	}

	@Override
	public void add(Sms sms) {
		smss.add(sms);
		notifyDataSetChanged();
		super.add(sms);
	}

	@Override
	public void remove(Sms sms) {
		smss.remove(sms);
		notifyDataSetChanged();
		super.remove(sms);
	}
}