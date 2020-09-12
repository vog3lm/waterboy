package de.sit.waterboy.application;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.sit.waterboy.common.Properties;
import de.sit.waterboy.R;

class Water extends Mode {

    Water(Mode mode){
        super(mode.listing,mode.menu,mode.store);
        this.onCreate();
    }
    @Override
    void onCreate(){
        /**/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(super.listing.getApplicationContext());
        int normal = preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL);
        int urgent = preferences.getInt(Properties.COLOR_URGENT_KEY,Properties.COLOR_URGENT);
        int danger = preferences.getInt(Properties.COLOR_DANGER_KEY,Properties.COLOR_DANGER);
        float level = Float.valueOf(preferences.getString(Properties.LEVEL_DANGER_KEY,Properties.LEVEL_DANGER))/100;
        /**/
        FloatingActionButton create = super.listing.findViewById(R.id.create);
        create.setImageResource(R.drawable.ic_check_white_24dp);
        super.listing.findViewById(R.id.water).setVisibility(View.GONE);
        super.listing.findViewById(R.id.fertilize).setVisibility(View.GONE);
        super.listing.findViewById(R.id.soil).setVisibility(View.GONE);
        /**/
        LinearLayout list = super.listing.findViewById(R.id.listing);
        for(int i=0; i<list.getChildCount()-1; i++){
            View item = list.getChildAt(i);
            Model model = super.store.onRead((int) item.getTag());
            CheckBox box = item.findViewById(R.id.checkbox);
            box.setVisibility(View.VISIBLE);
            onColorize(box,model,level,normal,urgent,danger);
        }
        /**/
        ActionBar bar = super.listing.getSupportActionBar();
        if(null != bar){bar.setTitle("Water");}
        if(null != super.menu) {
            super.menu.getItem(0).setVisible(false);
        }
        /**/
        super.listing.mode = this;
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
                model.wc = model.wi;
                super.store.onUpdate(model);
                box.setChecked(false);
            }
        }
        new Default(this);
    }
    @Override
    void onClick(int pk){
        CheckBox box = super.listing.findViewById(R.id.listing).findViewWithTag(pk).findViewById(R.id.checkbox);
        box.setChecked(!box.isChecked());
    }
    @Override
    void onLong(int pk){
        CheckBox box = super.listing.findViewById(R.id.listing).findViewWithTag(pk).findViewById(R.id.checkbox);
        box.setChecked(!box.isChecked());
    }
    @Override
    void onBack(){this.onDestroy();}

    private void onDestroy(){
        LinearLayout list = super.listing.findViewById(R.id.listing);
        for(int i=0; i<list.getChildCount()-1; i++){
            CheckBox box = list.getChildAt(i).findViewById(R.id.checkbox);
            box.setChecked(false);
        }
        new Default(this);
    }

    protected void onColorize(CheckBox box, Model model, float level, int normal, int urgent, int danger){
        if(0 > model.wc){box.setButtonTintList(ColorStateList.valueOf(urgent));}
        else if(level*model.wi > model.wc){box.setButtonTintList(ColorStateList.valueOf(danger));}
        else{box.setButtonTintList(ColorStateList.valueOf(normal));}
    }
    @Override
    protected void onHide(){
        float level = Float.valueOf(PreferenceManager.getDefaultSharedPreferences(super.listing.getApplicationContext()).getString(Properties.LEVEL_DANGER_KEY,Properties.LEVEL_DANGER))/100;
        LinearLayout listing = this.listing.findViewById(R.id.listing);
        for(int i=0; i<listing.getChildCount()-1; i++){
            View item = listing.getChildAt(i);
            Model model = super.store.onRead((int) item.getTag());
            if(level*model.wi < model.wc){item.setVisibility(View.GONE);}
        }
    }
}
