package com.uncannyvision.apps.imagerecognitiondemo;

import com.uncannyvision.apps.demo_vodafone_similarity_and_recognition.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}