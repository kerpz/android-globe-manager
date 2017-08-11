package com.ohmnismart.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class ServiceUSSD extends AccessibilityService {
	//public static String TAG = ServiceUSSD.class.getSimpleName();

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		//Log.d(TAG, "onAccessibilityEvent");
		if (event.getClassName().equals("android.app.AlertDialog")) {
			String text = event.getText().toString();

			Intent intent = new Intent("com.ohmnismart.ussd.action.REFRESH");
            intent.putExtra("text", text);
			sendBroadcast(intent);
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
}