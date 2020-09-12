package de.sit.waterboy.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceManager;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import de.sit.waterboy.common.Properties;
import de.sit.waterboy.R;

public class ColorDialog extends PreferenceDialogFragmentCompat implements ColorPicker.OnColorChangedListener {

    private SharedPreferences preferences;
    private ColorPreference preference;
    private ColorPicker picker;
    private TextView hex;
    private EditText lvl;

    ColorDialog(ColorPreference preference){
        this.preference = preference;
        final Bundle bundle = new Bundle(1);
        bundle.putString(ColorDialog.ARG_KEY, preference.getKey());
        this.setArguments(bundle);
    }
    @Override
    public View onCreateDialogView(Context context){
        View view = super.onCreateDialogView(context);
        if(R.id.color == view.getId()){
            this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
            int defclr = Properties.COLOR_URGENT;
            if(Properties.COLOR_NORMAL_KEY.equals(this.preference.getKey())){defclr = Properties.COLOR_NORMAL;}
            else if(Properties.COLOR_LAYOUT_KEY.equals(this.preference.getKey())){defclr = Properties.COLOR_LAYOUT;}
            else if(Properties.COLOR_DANGER_KEY.equals(this.preference.getKey())){defclr = Properties.COLOR_DANGER;}
            int color = this.preferences.getInt(this.preference.getKey(), defclr);
            this.picker = view.findViewById(R.id.picker);
            this.picker.addSVBar((SVBar) view.findViewById(R.id.svbar));
            this.picker.addSaturationBar((SaturationBar) view.findViewById(R.id.saturationbar));
            this.picker.addValueBar((ValueBar) view.findViewById(R.id.valuebar));
            this.picker.setColor(color);
            this.picker.setOnColorChangedListener(this);
            this.hex = view.findViewById(R.id.hex);
            this.hex.setText(String.format("#%06X",(0xFFFFFF & color)));
            if(Properties.COLOR_DANGER_KEY.equals(this.preference.getKey())){
                this.lvl = view.findViewById(R.id.lvl);
                this.lvl.setVisibility(View.VISIBLE);
                this.lvl.setText(this.preferences.getString(Properties.LEVEL_DANGER_KEY,Properties.LEVEL_DANGER));
            }
        }
        return view;
    }
    @Override
    public void onDialogClosed(boolean result){
        if(result){
            int color = this.picker.getColor();
            this.preferences.edit().putInt(this.preference.getKey(),color).apply();
            if(Properties.COLOR_DANGER_KEY.equals(this.preference.getKey())){
                String level = this.lvl.getText().toString();
                this.preferences.edit().putString(Properties.LEVEL_DANGER_KEY,level).apply();
                this.preference.setSummary(color,level);
            }else{this.preference.setSummary(color);}

        }
    }
    @Override
    public void onColorChanged(int color){this.hex.setText(String.format("#%06X",(0xFFFFFF & color)));}
}