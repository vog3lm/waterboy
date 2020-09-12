package de.sit.waterboy.preferences;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import de.sit.waterboy.R;
import de.sit.waterboy.application.Model;
import de.sit.waterboy.application.Store;
import de.sit.waterboy.common.DialogImport;
import de.sit.waterboy.common.Dialogs;
import de.sit.waterboy.common.Properties;

public class Importer extends AppCompatActivity implements View.OnClickListener
                                                         , DialogImport {
    private final String root = Environment.getExternalStorageDirectory().getPath() + "/Download";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.listing);
        /**/
        File directory = new File(root);
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(0 != files.length){
                LinearLayout listing = super.findViewById(R.id.listing);
                for(File file : files){
                    if(file.getName().contains(".waterboy.csv")){listing.addView(this.onCreateItem(file.getName()));}
                }
            }else{
                /* TODO :: add manual, add file with ending to Download directory... */
            }
        }
        /**/
        ActionBar bar = super.getSupportActionBar();
        if(null != bar){
            bar.setTitle("Import Data");
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setBackgroundDrawable(new ColorDrawable(PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).getInt(Properties.COLOR_LAYOUT_KEY,Properties.COLOR_LAYOUT)));
        }
    }

    private View onCreateItem(String source){
        LinearLayout.LayoutParams lpl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpl.setMargins(8, 0, 8, 0);
        TextView item = new TextView(super.getApplicationContext());
        item.setOnClickListener(this);
        item.setText(source);
        item.setLayoutParams(lpl);
        item.setTag(source);
        item.setPadding(0,8,0,8);
        item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        return item;
    }
    @Override
    public void onClick(View view){new Dialogs(this).onImport((String) view.getTag(),this); }
    @Override
    public void onImport(String source){
        List<String[]> records;
        try{
            FileReader file = new FileReader(this.root+"/"+source);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader reader = new CSVReaderBuilder(file).withCSVParser(parser).build(); // .withSkipLines(1)
            records = reader.readAll();
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        Store store = new Store(this.getApplicationContext());
        store.onUpgrade(store.getWritableDatabase(),0,1);
        for(String[] record : records){
            store.onInsert(new Model(record));
        }
        super.setResult(AppCompatActivity.RESULT_OK, new Intent());
        super.finish();
    }
}
