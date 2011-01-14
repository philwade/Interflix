package org.philwade.android.interflix;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class InterflixPreferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
		PreferenceManager manager = getPreferenceManager();
		manager.setSharedPreferencesName(InterFlix.PREFS_FILE);
	}
}
