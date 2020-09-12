package de.sit.waterboy.application;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.sit.waterboy.common.DialogDeletable;
import de.sit.waterboy.common.DialogUpdatable;
import de.sit.waterboy.common.Dialogs;
import de.sit.waterboy.common.Properties;
import de.sit.waterboy.R;

public class Details extends AppCompatActivity implements DialogUpdatable
                                                        , DialogDeletable
                                                        , View.OnClickListener {
    private Model model;

    private EditText name;
    private EditText location;
    private EditText wi;
    private EditText wc;
    private EditText fi;
    private EditText fc;
    private EditText si;
    private EditText sc;
    private EditText description;

    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.details);
        /**/
        Bundle extras = super.getIntent().getExtras();
        if(null == extras){super.onBackPressed();}
        if(!extras.containsKey(Properties.REQUEST_CODE)
        || !extras.containsKey(Properties.REQUEST_RECORD)){super.onBackPressed();}
        this.model =(Model) extras.getSerializable(Properties.REQUEST_RECORD);
        if(null == this.model){super.onBackPressed();}
        /**/
        this.onContent(this.model);
        /**/
        this.code = extras.getInt(Properties.REQUEST_CODE);
        String title = super.getResources().getString(R.string.app_name);
        if(Properties.REQUEST_CREATE == this.code){title = this.onCreate();}
        else if(Properties.REQUEST_DETAIL == this.code){title = this.onDetails();}
        else{super.onBackPressed();}
        /**/
        ActionBar bar = super.getSupportActionBar();
        if(null != bar){
            bar.setTitle(title);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setBackgroundDrawable(new ColorDrawable(PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).getInt(Properties.COLOR_LAYOUT_KEY,Properties.COLOR_LAYOUT)));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        this.getMenuInflater().inflate(R.menu.details, menu);
        if(Properties.REQUEST_CREATE == this.code){menu.getItem(1).setVisible(false);}
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(android.R.id.home == id){
            if(isDataChanged()){new Dialogs(this).onUpdate(this);}
            else{this.onBack(RESULT_CANCELED,RESULT_CANCELED);}
            return true;
        }else if(R.id.delete == id){
            new Dialogs(this).onDelete(this.model.pk,this);
            return true;
        }else if(R.id.undo == id){
            this.onContent(this.model);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view){
        int id = view.getId();
        if(R.id.water == id){
            if(Properties.REQUEST_CREATE == this.code){this.onUpdate(true);}
            else{
                this.model.wc = this.model.wi;
                FloatingActionButton water = super.findViewById(R.id.water);
                water.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }
        }else if(R.id.fertilize == id){
            this.model.fc = this.model.fi;
            FloatingActionButton water = super.findViewById(R.id.fertilize);
            water.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }else if(R.id.soil == id){
            this.model.sc = this.model.si;
            FloatingActionButton water = super.findViewById(R.id.soil);
            water.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }
    }

    private void onBack(int result, int response){
        Intent intent = super.getIntent();
        if(RESULT_OK == result) {
            Bundle extras = intent.getExtras();
            extras.putSerializable(Properties.REQUEST_RECORD, model);
            intent.putExtras(extras);
            super.setResult(response, intent);
        }else{
            super.setResult(response, intent);
            super.onBackPressed();
        }
        super.finish();
    }
    @Override
    public void onBackPressed(){
        if(isDataChanged()){new Dialogs(this).onUpdate(this);}
        else{this.onBack(AppCompatActivity.RESULT_CANCELED,AppCompatActivity.RESULT_CANCELED);}
    }

    private boolean isDataChanged(){
        if(!this.name.getText().toString().trim().equals(this.model.name)){return true;}
        if(!this.location.getText().toString().trim().equals(this.model.location)){return true;}
        if(!this.wi.getText().toString().trim().equals(String.valueOf(this.model.wi))){return true;}
        if(!this.wc.getText().toString().trim().equals(String.valueOf(this.model.wc))){return true;}
        if(!this.fi.getText().toString().trim().equals(String.valueOf(this.model.fi))){return true;}
        if(!this.fc.getText().toString().trim().equals(String.valueOf(this.model.fc))){return true;}
        if(!this.si.getText().toString().trim().equals(String.valueOf(this.model.si))){return true;}
        return !this.sc.getText().toString().trim().equals(String.valueOf(this.model.sc));
    }
    @Override
    public void onUpdate(boolean result){
        if(result){
            Validator validate = new Validator();
            String name = validate.notEmpty(this.name);
            // validate.integerAndNotEmpty(this.wi);
            // validate.integerAndNotEmpty(this.fi);
            // validate.integerAndNotEmpty(this.si);
            if(validate.hasErrors){return;}
            /* TODO :: validate input */
            this.model.name = name;
            this.model.location = this.location.getText().toString().trim();
            this.model.wi = Integer.valueOf(this.wi.getText().toString().trim());
            this.model.wc = Integer.valueOf(this.wc.getText().toString().trim());
            this.model.fi = Integer.valueOf(this.fi.getText().toString().trim());
            this.model.fc = Integer.valueOf(this.fc.getText().toString().trim());
            this.model.si = Integer.valueOf(this.si.getText().toString().trim());
            this.model.sc = Integer.valueOf(this.sc.getText().toString().trim());
            this.model.description = this.description.getText().toString().trim();
            this.onBack(RESULT_OK, Properties.REQUEST_UPDATE);
        }else{
            this.onBack(RESULT_CANCELED, RESULT_CANCELED);
        }

    }
    @Override
    public void onDelete(int index){this.onBack(RESULT_OK,Properties.REQUEST_DELETE);}

    private void onContent(Model model){
        this.name = super.findViewById(R.id.name);
        this.name.setText(model.name);
        this.location = super.findViewById(R.id.location);
        this.location.setText(model.location);
        this.wi = super.findViewById(R.id.wi);
        this.wi.setText(String.valueOf(model.wi));
        this.wc = super.findViewById(R.id.wc);
        this.wc.setText(String.valueOf(model.wc));
        this.fi = super.findViewById(R.id.fi);
        this.fi.setText(String.valueOf(model.fi));
        this.fc = super.findViewById(R.id.fc);
        this.fc.setText(String.valueOf(model.fc));
        this.si = super.findViewById(R.id.si);
        this.si.setText(String.valueOf(model.si));
        this.sc = super.findViewById(R.id.sc);
        this.sc.setText(String.valueOf(model.sc));

        this.description = super.findViewById(R.id.description);
        this.description.setText(model.description);
    }

    private String onCreate(){
        FloatingActionButton water = super.findViewById(R.id.water);
        water.setOnClickListener(this);
        water.setSize(FloatingActionButton.SIZE_NORMAL);
        water.setImageResource(R.drawable.ic_check_white_24dp);
        water.setBackgroundTintList(ColorStateList.valueOf(PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL)));
        super.findViewById(R.id.fertilize).setVisibility(View.GONE);
        super.findViewById(R.id.soil).setVisibility(View.GONE);
        int normal = PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL);
        ImageView drop = super.findViewById(R.id.drop);
        drop.getDrawable().mutate().setColorFilter(normal, PorterDuff.Mode.MULTIPLY);
        ImageView flash = super.findViewById(R.id.flash);
        flash.getDrawable().mutate().setColorFilter(normal, PorterDuff.Mode.MULTIPLY);
        ImageView shovel = super.findViewById(R.id.shovel);
        shovel.getDrawable().mutate().setColorFilter(normal, PorterDuff.Mode.MULTIPLY);
        return "Create Plant";
    }

    private String onDetails(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext());
        int danger = preferences.getInt(Properties.COLOR_DANGER_KEY,Properties.COLOR_DANGER);
        float danger_level = Float.parseFloat(preferences.getString(Properties.LEVEL_DANGER_KEY,Properties.LEVEL_DANGER))/100;
        int urgent = preferences.getInt(Properties.COLOR_URGENT_KEY,Properties.COLOR_URGENT);
        int normal = preferences.getInt(Properties.COLOR_NORMAL_KEY,Properties.COLOR_NORMAL);
        /* create navigation */
        ImageView drop = super.findViewById(R.id.drop);
        FloatingActionButton water = super.findViewById(R.id.water);
        water.setOnClickListener(this);
        if(0 > this.model.wc){
            water.setBackgroundTintList(ColorStateList.valueOf(urgent));
            drop.getDrawable().mutate().setColorFilter(urgent, PorterDuff.Mode.MULTIPLY);
        }else if(danger_level*this.model.wi > this.model.wc){
            water.setBackgroundTintList(ColorStateList.valueOf(danger));
            drop.getDrawable().mutate().setColorFilter(danger, PorterDuff.Mode.MULTIPLY);
        }else{
            water.setBackgroundTintList(ColorStateList.valueOf(normal));
            drop.getDrawable().mutate().setColorFilter(normal, PorterDuff.Mode.MULTIPLY);
        }
        ImageView flash = super.findViewById(R.id.flash);
        FloatingActionButton fertilize = super.findViewById(R.id.fertilize);
        fertilize.setOnClickListener(this);
        if(0 > this.model.fc){
            fertilize.setBackgroundTintList(ColorStateList.valueOf(urgent));
            flash.getDrawable().mutate().setColorFilter(urgent, PorterDuff.Mode.MULTIPLY);
        }else if(danger_level*this.model.fi > this.model.fc){
            fertilize.setBackgroundTintList(ColorStateList.valueOf(danger));
            flash.getDrawable().mutate().setColorFilter(danger, PorterDuff.Mode.MULTIPLY);
        }else{
            fertilize.setBackgroundTintList(ColorStateList.valueOf(normal));
            flash.getDrawable().mutate().setColorFilter(normal, PorterDuff.Mode.MULTIPLY);
        }
        ImageView shovel = super.findViewById(R.id.shovel);
        FloatingActionButton soil = super.findViewById(R.id.soil);
        soil.setOnClickListener(this);
        if(0 > this.model.sc){
            soil.setBackgroundTintList(ColorStateList.valueOf(urgent));
            shovel.getDrawable().mutate().setColorFilter(urgent, PorterDuff.Mode.MULTIPLY);
        }else if(danger_level*this.model.si > this.model.sc){
            soil.setBackgroundTintList(ColorStateList.valueOf(danger));
            shovel.getDrawable().mutate().setColorFilter(danger, PorterDuff.Mode.MULTIPLY);
        }else{
            soil.setBackgroundTintList(ColorStateList.valueOf(normal));
            shovel.getDrawable().mutate().setColorFilter(normal, PorterDuff.Mode.MULTIPLY);
        }
        return this.model.name;
    }
}
