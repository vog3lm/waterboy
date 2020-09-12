package de.sit.waterboy.common;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Dialogs {

    private final Activity activity;

    public Dialogs(Activity activity){
        this.activity = activity;
    }

    private AlertDialog.Builder dialogCreate(String prefix, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(prefix + " Record")
               .setMessage("Do you really want to "+prefix.toLowerCase()+" this record?")
               .setPositiveButton("Yes",listener)
               .setNegativeButton("No",listener);
        return builder;
    }
    /* Usage :: Importer */
    public void onImport(final String source, final DialogImport operator){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dif, int result) {
                if(DialogInterface.BUTTON_POSITIVE == result){operator.onImport(source);}
            }
        };
        this.dialogCreate("Import",listener)
        .setMessage("Do you really want to import "+source+"? This operation will overwrite your current data set!")
        .create().show();
    }
    /* Usage :: Details */
    public void onUpdate(final DialogUpdatable operator){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dif, int result) {
                if(DialogInterface.BUTTON_POSITIVE == result){operator.onUpdate(true);}
                else if(DialogInterface.BUTTON_NEGATIVE == result){operator.onUpdate(false);}
            }
        };
        this.dialogCreate("Update",listener).create().show();
    }
    /* Usage :: Details, Default */
    public void onDelete(final int index, final DialogDeletable operator){
        this.dialogCreate("Delete", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dif, int result){
                if(DialogInterface.BUTTON_POSITIVE == result){operator.onDelete(index);}
            }
        }).create().show();
    }
}
