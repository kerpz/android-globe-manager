package com.globe.ui;

import com.ohmnismart.ui.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class ActivityPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Preferences");

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
			super.onResume();
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

			for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
				Preference preference = getPreferenceScreen().getPreference(i);
				if (preference instanceof PreferenceGroup) {
					PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
					for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
						Preference singlePref = preferenceGroup.getPreference(j);
						updatePreference(singlePref);
					}
				}
				else {
					updatePreference(preference);
				}
			}
		}

		@Override
		public void onPause() {
			getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
			super.onPause();
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			updatePreference(findPreference(key));
		}

		private void updatePreference(Preference preference) {
			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				preference.setSummary(listPreference.getEntry());
			}
			else if (preference instanceof EditTextPreference) {
				EditTextPreference editTextPrference = (EditTextPreference) preference;
				preference.setSummary(editTextPrference.getText());
			}
			else if (preference instanceof PreferenceDate) {
				PreferenceDate datePreference = (PreferenceDate) preference;
				preference.setSummary(datePreference.getText());
			}
			else if (preference instanceof PreferenceTime) {
				PreferenceTime timePreference = (PreferenceTime) preference;
				preference.setSummary(timePreference.getText());
			}
        }
	}

}