package de.sit.waterboy.application;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;

import de.sit.waterboy.R;
import de.sit.waterboy.common.Properties;

class Fertilize extends Water {

    Fertilize(Mode mode){
        super(mode);
        ActionBar bar = super.listing.getSupportActionBar();
        if(null != bar){bar.setTitle("Fertilize");}
    }
    @Override
    void onPrimary(){
        LinearLayout list = super.listing.findViewById(R.id.listing);
        for(int i=0; i<list.getChildCount()-1; i++){
            View item = list.getChildAt(i);
            CheckBox box = item.findViewById(R.id.checkbox);
            if(box.isChecked()){
                int pk = (int) item.getTag();
                Model model = super.store.onRead(pk);
                model.fc = model.fi;
                super.store.onUpdate(model);
                box.setChecked(false);
            }
        }
        new Default(this);
    }
    @Override
    protected void onColorize(CheckBox box, Model model, float level, int normal, int urgent, int danger){
        if( 0 > model.fc){box.setButtonTintList(ColorStateList.valueOf(urgent));}
        else if(level*model.fi > model.fc){box.setButtonTintList(ColorStateList.valueOf(danger));}
        else{box.setButtonTintList(ColorStateList.valueOf(normal));}
    }
    @Override
    protected void onHide(){
        float level = Float.valueOf(PreferenceManager.getDefaultSharedPreferences(super.listing.getApplicationContext()).getString(Properties.LEVEL_DANGER_KEY,Properties.LEVEL_DANGER))/100;
        LinearLayout listing = this.listing.findViewById(R.id.listing);
        for(int i=0; i<listing.getChildCount()-1; i++){
            View item = listing.getChildAt(i);
            Model model = super.store.onRead((int) item.getTag());
            if(level*model.fi < model.fc){item.setVisibility(View.GONE);}
        }
    }
}
