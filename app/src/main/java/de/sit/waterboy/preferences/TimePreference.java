package de.sit.waterboy.preferences;

import android.content.Context;
import android.util.AttributeSet;


import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;

import de.sit.waterboy.common.Properties;
import de.sit.waterboy.R;

public class TimePreference extends DialogPreference {

    public TimePreference(Context context, AttributeSet set){
        super(context, set);
        super.setPositiveButtonText("Ok");
        super.setNegativeButtonText("Cancel");
        super.setDialogTitle("Set Reminder Time");
        super.setDialogLayoutResource(R.layout.time);
        super.setSummary(PreferenceManager.getDefaultSharedPreferences(context).getString(Properties.REMINDER_TIME,"11:00"));
    }

}