package de.sit.waterboy.preferences;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import de.sit.waterboy.common.Properties;
import de.sit.waterboy.R;

public class Preferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle sis) {
        super.onCreate(sis);
        super.setContentView(R.layout.preferences);
        super.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new Fragment())
                .commit();
        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setBackgroundDrawable(new ColorDrawable(PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).getInt(Properties.COLOR_LAYOUT_KEY,Properties.COLOR_LAYOUT)));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.getMenuInflater().inflate(R.menu.preferences, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(R.id.defaults == item.getItemId()){
            /* TODO :: load defaults */
        }
        return super.onOptionsItemSelected(item);
    }
}