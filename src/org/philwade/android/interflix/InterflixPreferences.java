package org.philwade.android.interflix;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class InterflixPreferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
	}
}
