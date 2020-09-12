package de.sit.waterboy.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.sit.waterboy.common.Properties;
import de.sit.waterboy.R;

public class ColorPreference extends DialogPreference {

    private boolean mutex = true;

    private FloatingActionButton fab;
    private ImageView icon;
    private TextView level;

    public ColorPreference(Context context, AttributeSet set){
        super(context, set);
        super.setPositiveButtonText("Ok");
        super.setNegativeButtonText("Cancel");
        super.setDialogLayoutResource(R.layout.color);
    }
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        if(this.mutex) {
            int defclr = Properties.COLOR_URGENT;
            if(Properties.COLOR_NORMAL_KEY.equals(super.getKey())){defclr = Properties.COLOR_NORMAL;}
            else if(Properties.COLOR_LAYOUT_KEY.equals(super.getKey())){defclr = Properties.COLOR_LAYOUT;}
            else if(Properties.COLOR_DANGER_KEY.equals(super.getKey())){defclr = Properties.COLOR_DANGER;}
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(super.getContext());
            int color = preferences.getInt(super.getKey(), defclr);
            this.fab = new FloatingActionButton(super.getContext());
            this.fab.setImageResource(R.drawable.ic_drop_white_24dp);
            this.fab.setBackgroundTintList(ColorStateList.valueOf(color));
            this.fab.setSize(FloatingActionButton.SIZE_MINI);
            LinearLayout.LayoutParams lpi = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lpi.setMargins(24, 0, 24, 0);
            this.icon = new ImageView(super.getContext());
            this.icon.setLayoutParams(lpi);
            this.icon.setImageResource(R.drawable.ic_drop_white_24dp);
            this.icon.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            /*  https://android.googlesource.com/platform/frameworks/base/+/master/core/res/res/layout/preference.xml
                LinearLayout
                    ImageView       (@+android:id/icon)
                    RelativeLayout
                        TextView    (@+android:id/title)
                        TextView    (@+android:id/summary)
                    LinearLayout    (@+android:id/widget_frame) */
            LinearLayout wrapper = (LinearLayout) holder.findViewById(android.R.id.widget_frame);
            wrapper.setOrientation(LinearLayout.HORIZONTAL);
            if(Properties.COLOR_DANGER_KEY.equals(super.getKey())){
                LinearLayout.LayoutParams lpl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpl.setMargins(0, 0, 48, 0);
                this.level = new TextView(super.getContext());
                this.level.setLayoutParams(lpl);
                this.level.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                this.level.setText(preferences.getString(Properties.LEVEL_DANGER_KEY,Properties.LEVEL_DANGER)+"%");
                wrapper.addView(this.level);
            }
            wrapper.addView(this.icon);
            wrapper.addView(this.fab);
            wrapper.setVisibility(View.VISIBLE);

            this.mutex = false;
        }
    }
    @Override
    public void setSummary(int color){
        this.icon.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        this.fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    void setSummary(int color, String level){
        this.setSummary(color);
        this.level.setText(level+"%");
    }
}