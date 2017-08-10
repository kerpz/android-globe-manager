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
import android.widget.Toast;

public class DialogFragmentAddSim extends DialogFragment {

	EditText etNumber;
	EditText etExpire;
	EditText etBalance;
	EditText etBalanceExpire;
	LinearLayout submitLayout;

	public static final String ARG_ITEM_ID = "sim_add_dialog_fragment";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
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

		//setValue();

		builder.setTitle(R.string.add_sim);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.add,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Sim sim = new Sim();
						SimModel db = new SimModel(getActivity());
						// check and correct
						String iNumber = etNumber.getText().toString();
						if (iNumber.length() == 11 && iNumber.charAt(0) == '0') {
							iNumber = iNumber.substring(1);
						}
						else if (iNumber.length() == 12 && iNumber.charAt(0) == '6' &&
								 iNumber.charAt(1) == '3') {
							iNumber = iNumber.replaceFirst("63","");;
						}
						else if (iNumber.length() == 13 && iNumber.charAt(0) == '+' &&
								iNumber.charAt(1) == '6' && iNumber.charAt(2) == '3') {
							iNumber = iNumber.replaceFirst("\\+63","");;
						}
						else {
							Toast.makeText(getActivity(), "Invalid phone number!", Toast.LENGTH_SHORT).show();
							return;
						}
						
						sim.setNumber(iNumber);
						sim.setExpire(etExpire.getText().toString());
						sim.setBalance(etBalance.getText().toString());
						sim.setBalanceExpire(etBalanceExpire.getText().toString());
						db.addSim(sim);

						FragmentListSim fragmentListSim = (FragmentListSim) getFragmentManager()
								.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
						if (fragmentListSim != null) {
							fragmentListSim.updateView();
						}
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

}
