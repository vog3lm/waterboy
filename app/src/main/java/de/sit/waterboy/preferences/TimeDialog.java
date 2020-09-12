package de.sit.waterboy.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

import de.sit.waterboy.common.Properties;

public class TimeDialog extends PreferenceDialogFragmentCompat {

    private SharedPreferences preferences;
    private TimePreference preference;
    private TimePicker picker;

    TimeDialog(TimePreference preference){
        this.preference = preference;
        final Bundle bundle = new Bundle(1);
        bundle.putString(TimeDialog.ARG_KEY, preference.getKey());
        this.setArguments(bundle);
    }
    @Override
    public View onCreateDialogView(Context context){
        View view = super.onCreateDialogView(context);
        if(view instanceof TimePicker){
            this.picker = (TimePicker) view;
            this.picker.setIs24HourView(true);
            this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String[] split = this.preferences.getString(Properties.REMINDER_TIME,"11:00").split(":");
            this.picker.setHour(Integer.parseInt(split[0]));
            this.picker.setMinute(Integer.parseInt(split[1]));
        }
        return view;
    }
    @Override
    public void onDialogClosed(boolean result){
        String time = String.format(Locale.getDefault(),"%02d",this.picker.getHour())
                 +":"+String.format(Locale.getDefault(),"%02d",this.picker.getMinute());
        if(result){this.preferences.edit().putString(Properties.REMINDER_TIME,time).apply();}
        this.preference.setSummary(time);
    }
}