package com.ohmnismart.ui;

import com.ohmnismart.db.Sim;
import com.ohmnismart.db.SimModel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DialogFragmentEditSim extends DialogFragment {

	EditText etNumber;
	EditText etExpire;
	EditText etBalance;
	EditText etBalanceExpire;
	LinearLayout submitLayout;
	
	Sim sim;
	
	public static final String ARG_ITEM_ID = "sim_edit_dialog_fragment";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle bundle = this.getArguments();
		sim = bundle.getParcelable("selectedDevice");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View customDialogView = inflater.inflate(R.layout.fragment_dialog_sim, (ViewGroup) getView(), false);
		builder.setView(customDialogView);

		etNumber = (EditText) customDialogView.findViewById(R.id.etNumber);
		etExpire = (EditText) customDialogView.findViewById(R.id.etExpire);
		etBalance = (EditText) customDialogView.findViewById(R.id.etBalance);
		etBalanceExpire = (EditText) customDialogView.findViewById(R.id.etBalanceExpire);
		submitLayout = (LinearLayout) customDialogView
				.findViewById(R.id.layout_submit);
		submitLayout.setVisibility(View.GONE);

		setValue();

		builder.setTitle(R.string.edit_sim);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.edit,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Sim sim = new Sim();
						SimModel db = new SimModel(getActivity()); 
						sim.setNumber(etNumber.getText().toString());
						sim.setExpire(etExpire.getText().toString());
						sim.setBalance(etBalance.getText().toString());
						sim.setBalanceExpire(etBalanceExpire.getText().toString());
						db.updateSim(sim);
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = builder.create();

		return alertDialog;
	}

	private void setValue() {
		if (sim != null) {
			etNumber.setText(sim.getNumber());
			etExpire.setText(sim.getExpire());
			etBalance.setText(sim.getBalance());
			etBalanceExpire.setText(sim.getBalanceExpire());
		}
	}

}