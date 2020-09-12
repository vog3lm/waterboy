package de.sit.waterboy.preferences;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.sit.waterboy.R;
import de.sit.waterboy.application.Model;
import de.sit.waterboy.application.Store;
import de.sit.waterboy.common.Properties;

class Fragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle sis, String key){
        super.setPreferencesFromResource(R.xml.preferences, key);
        SwitchPreferenceCompat swp = super.findPreference(Properties.REMINDER_FLAG);
        if(null == Properties.getReminderIntent(super.getContext(),false)){swp.setChecked(false);}
        else{swp.setChecked(true);}
    }
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if(preference instanceof TimePreference){this.loadDialogPreference(new TimeDialog((TimePreference) preference));}
        else if(preference instanceof ColorPreference){this.loadDialogPreference(new ColorDialog((ColorPreference) preference));}
        else{super.onDisplayPreferenceDialog(preference);}
    }
    @Override
    public void onResume(){
        super.onResume();
        super.getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause(){
        super.onPause();
        super.getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences settings, String key){
        boolean reminder_flag = settings.getBoolean(Properties.REMINDER_FLAG,false);
        if(Properties.REMINDER_FLAG.equals(key)){
            if(reminder_flag){this.activateReminder(super.getContext(),settings);}
            else{
                AlarmManager am = (AlarmManager) super.getContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(Properties.getReminderIntent(super.getContext(),true));
            }
        }else if(Properties.REMINDER_TIME.equals(key) && reminder_flag){
            this.activateReminder(super.getContext(),settings);
        }else if(Properties.REMINDER_REDO.equals(key)){
            /* TODO :: 1 <= snooze <= 23 */
        }
    }
    @Override
    public boolean onPreferenceTreeClick(Preference preference){
        String key = preference.getKey();
        if(Properties.IMPORT_KEY.equals(key)){
            this.startActivityForResult(new Intent(super.getContext(), Importer.class), Properties.REQUEST_IMPORT);
            return true;
        }else if(Properties.EXPORT_KEY.equals(key)){
            try{
                SimpleDateFormat formatter= new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss", Locale.getDefault());
                String root = Environment.getExternalStorageDirectory().getPath()+"/Download/export-"+formatter.format(new Date(System.currentTimeMillis()))+".waterboy.csv";
                ArrayList<String[]> models = new ArrayList<>();
                for(Model model : new Store(super.getContext()).onRead(null,null)){
                    models.add(model.toArray());
                }
                CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(root), StandardCharsets.UTF_8), ';',CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
                writer.writeAll(models);
                writer.close();
                Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScannerIntent.setData(Uri.fromFile(new File(root)));
                super.getActivity().sendBroadcast(mediaScannerIntent);
                Toast.makeText(super.getContext(),"Export successful! "+root, Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                Toast.makeText(super.getContext(),"Export error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }
    @Override
    public void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);
        if(Properties.REQUEST_IMPORT == request && Activity.RESULT_OK == result){
            Toast.makeText(super.getContext(),"Import successful!", Toast.LENGTH_SHORT).show();
        }
    }

    private void activateReminder(Context context, SharedPreferences settings){
        String[] time = settings.getString(Properties.REMINDER_TIME,"11:00").split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,Properties.getReminderIntent(context,true));
    }

    private void loadDialogPreference(DialogFragment dialog){
        dialog.setTargetFragment(this, 0);
        dialog.show(super.getFragmentManager(), null);
    }
}
