package de.sit.waterboy.application;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import de.sit.waterboy.R;
import de.sit.waterboy.common.Properties;
import de.sit.waterboy.preferences.Preferences;

public class Listing extends AppCompatActivity implements View.OnClickListener
                                                        , View.OnLongClickListener {
    Mode mode;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout);
        this.onPermissions();
        /**/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext());
        /* create navigation */
        FloatingActionButton create = super.findViewById(R.id.create);
        create.setOnClickListener(this);
        create.setBackgroundTintList(ColorStateList.valueOf(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
        FloatingActionButton water = super.findViewById(R.id.water);
        water.setOnClickListener(this);
        water.setBackgroundTintList(ColorStateList.valueOf(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
        FloatingActionButton fertilize = super.findViewById(R.id.fertilize);
        fertilize.setOnClickListener(this);
        fertilize.setBackgroundTintList(ColorStateList.valueOf(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
        FloatingActionButton soil = super.findViewById(R.id.soil);
        soil.setOnClickListener(this);
        soil.setBackgroundTintList(ColorStateList.valueOf(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
        /* create list */
        Store store = new Store(this.getApplicationContext());
        LinearLayout listing = super.findViewById(R.id.listing);
        for(Model model : store.onRead(null,null)){
            listing.addView(this.onCreateItem(model));
        }
        listing.addView(super.getLayoutInflater().inflate(R.layout.padding,null));
        this.mode = new Default(this,null,store);
        /* detect first start */
        if(!preferences.getBoolean(Properties.IS_DEFLOWERED, false)){
            preferences.edit().putBoolean(Properties.IS_DEFLOWERED, true).apply();
            /* start database update service */
            AlarmManager manager = (AlarmManager) super.getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,Calendar.getInstance().getTimeInMillis(),/*2*60*1000*/ AlarmManager.INTERVAL_DAY,Properties.getDeamonIntent(this,true));
        }
        /* adapt theme */
        ActionBar bar = super.getSupportActionBar();
        if(null != bar){
            bar.setBackgroundDrawable(new ColorDrawable(PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).getInt(Properties.COLOR_LAYOUT_KEY,Properties.COLOR_LAYOUT)));
        }
    }

    View onCreateItem(Model model){
        View xml = super.getLayoutInflater().inflate(R.layout.item, null);
        View item = xml.findViewById(R.id.item);
        item.setTag(model.pk);
        item.setOnClickListener(this);
        item.setOnLongClickListener(this);
        this.onDecorateItem(item,model);
        return item;
    }

    void onDecorateItem(View item, Model model){
        TextView label = item.findViewById(R.id.label);
        label.setText(model.name);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext());
        int danger = preferences.getInt(Properties.COLOR_DANGER_KEY,Properties.COLOR_DANGER);
        float danger_level = Float.valueOf(preferences.getString(Properties.LEVEL_DANGER_KEY,Properties.LEVEL_DANGER))/100;
        int urgent =preferences.getInt(Properties.COLOR_URGENT_KEY,Properties.COLOR_URGENT);

        LinearLayout wrapper = item.findViewById(R.id.state);
        wrapper.removeAllViews();
        if(0 > model.wc){wrapper.addView(this.onColorizeState(R.drawable.ic_drop_white_24dp,urgent));}
        else if(danger_level*model.wi > model.wc){wrapper.addView(this.onColorizeState(R.drawable.ic_drop_white_24dp,danger));}
        if(0 > model.fc){wrapper.addView(this.onColorizeState(R.drawable.ic_flash_white_24dp,urgent));}
        else if(danger_level*model.fi > model.fc){wrapper.addView(this.onColorizeState(R.drawable.ic_flash_white_24dp,danger));}
        if(0 > model.sc){wrapper.addView(this.onColorizeState(R.drawable.ic_shovel_white_24dp,urgent));}
        else if(danger_level*model.si > model.sc){wrapper.addView(this.onColorizeState(R.drawable.ic_shovel_white_24dp,danger));}
    }

    private View onColorizeState(int icon, int color){
        ImageView state = super.getLayoutInflater().inflate(R.layout.state,null).findViewById(R.id.state);
        state.setImageResource(icon);
        state.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        return state;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.getMenuInflater().inflate(R.menu.listing, menu);
        this.mode.menu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(R.id.preferences == id){this.startActivityForResult(new Intent(super.getApplicationContext(), Preferences.class),Properties.REQUEST_CONFIG);}
        else if(R.id.filter == id){this.mode.onSecondary();}
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view){
        int id = view.getId();
        if(id == R.id.item){this.mode.onClick((int) view.getTag());}
        else if(R.id.create == id){this.mode.onPrimary();}
        else if(R.id.water == id){new Water(this.mode);}
        else if(R.id.fertilize == id){new Fertilize(this.mode);}
        else if(R.id.soil == id){new Soil(this.mode);}
    }
    @Override
    public boolean onLongClick(View view){
        if(R.id.item == view.getId()){this.mode.onLong((int) view.getTag());}
        return true;
    }
    @Override
    public void onBackPressed(){this.mode.onBack();}
    @Override
    public void startActivityForResult(Intent intent, int request){
        intent.putExtra(Properties.REQUEST_CODE, request);
        super.startActivityForResult(intent, request);
    }
    @Override
    public void onActivityResult(int request, int result, Intent intent){
        super.onActivityResult(request, result, intent);
        if(Properties.REQUEST_DETAIL == request){
            Bundle extras = intent.getExtras();
            if(null == extras){return;}
            Model model = (Model) extras.getSerializable(Properties.REQUEST_RECORD);
            if(Properties.REQUEST_UPDATE == result && null != model){
                this.mode.store.onUpdate(model);
                this.onDecorateItem(super.findViewById(R.id.listing).findViewWithTag(model.pk),model);
            }else if(Properties.REQUEST_DELETE == result && null != model){
                this.mode.store.onDelete(model.pk);
            }
        }else if(Properties.REQUEST_CREATE == request){
            Bundle extras = intent.getExtras();
            if(null == extras){return;}
            Model model = (Model) extras.getSerializable(Properties.REQUEST_RECORD);
            if(Properties.REQUEST_UPDATE == result && null != model){
                this.mode.store.onInsert(model);
                LinearLayout listing = super.findViewById(R.id.listing);
                listing.addView(this.onCreateItem(model),listing.getChildCount()-1);
            }
        }else if(Properties.REQUEST_CONFIG == request){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext());
            FloatingActionButton create = super.findViewById(R.id.create);
            create.setBackgroundTintList(ColorStateList.valueOf(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
            FloatingActionButton water = super.findViewById(R.id.water);
            water.setBackgroundTintList(ColorStateList.valueOf(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
            FloatingActionButton fertilize = super.findViewById(R.id.fertilize);
            fertilize.setBackgroundTintList(ColorStateList.valueOf(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
            FloatingActionButton soil = super.findViewById(R.id.soil);
            soil.setBackgroundTintList(ColorStateList.valueOf(preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
            ActionBar bar = super.getSupportActionBar();
            if(null != bar){
                bar.setBackgroundDrawable(new ColorDrawable(PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).getInt(Properties.COLOR_LAYOUT_KEY,Properties.COLOR_LAYOUT)));
            }
            /* create list */
            LinearLayout listing = super.findViewById(R.id.listing);
            listing.removeAllViews();
            for(Model model : this.mode.store.onRead(null,null)){
                listing.addView(this.onCreateItem(model));
            }
            listing.addView(super.getLayoutInflater().inflate(R.layout.padding,null));
        }
    }

    private void onPermissions(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            int REQUEST_EXTERNAL_STORAGE = 0;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            int REQUEST_EXTERNAL_STORAGE = 1;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            int REQUEST_CAMERA = 2;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
        }
    }
}
