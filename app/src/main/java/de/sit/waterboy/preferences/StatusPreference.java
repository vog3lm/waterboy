package de.sit.waterboy.preferences;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import java.util.Calendar;

import de.sit.waterboy.R;
import de.sit.waterboy.common.Properties;

public class StatusPreference extends Preference implements Preference.OnPreferenceClickListener {

    private ImageView icon;

    public StatusPreference(Context context, AttributeSet set){
        super(context, set);
        super.setOnPreferenceClickListener(this);
    }
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder){
        super.onBindViewHolder(holder);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(super.getContext());
        this.icon = new ImageView(super.getContext());
        this.icon.setImageResource(R.drawable.ic_dot_white_24dp);
        if(null != Properties.getDeamonIntent(super.getContext(),false)){this.icon.getDrawable().mutate().setColorFilter(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL), PorterDuff.Mode.MULTIPLY);}
        else{this.icon.getDrawable().mutate().setColorFilter(preferences.getInt(Properties.COLOR_URGENT_KEY,Properties.COLOR_URGENT), PorterDuff.Mode.MULTIPLY);}
        /*  https://android.googlesource.com/platform/frameworks/base/+/master/core/res/res/layout/preference.xml
            LinearLayout
                ImageView       (@+android:id/icon)
                RelativeLayout
                    TextView    (@+android:id/title)
                    TextView    (@+android:id/summary)
                LinearLayout    (@+android:id/widget_frame) */
        LinearLayout wrapper = (LinearLayout) holder.findViewById(android.R.id.widget_frame);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.addView(icon);
        wrapper.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onPreferenceClick(Preference preference){
        /* restart database update service */
        /*  deactivate alarm :: manager.cancel(pi); *//*
            android-sdk/platform-tools/adb shell dumpsys alarm | grep de.sit.waterboy */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(super.getContext());
        AlarmManager manager = (AlarmManager) super.getContext().getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),/*2*60*1000*/ AlarmManager.INTERVAL_DAY,Properties.getDeamonIntent(super.getContext(),true));
        if(null != Properties.getDeamonIntent(super.getContext(),false)){this.icon.getDrawable().mutate().setColorFilter(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL), PorterDuff.Mode.MULTIPLY);}
        else{this.icon.getDrawable().mutate().setColorFilter(preferences.getInt(Properties.COLOR_URGENT_KEY,Properties.COLOR_URGENT), PorterDuff.Mode.MULTIPLY);}
        return true;
    }
}