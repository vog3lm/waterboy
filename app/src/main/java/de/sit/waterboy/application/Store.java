package de.sit.waterboy.application;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Store extends SQLiteOpenHelper {

    private final String table = "plants";

    public Store(Context context){
        super(context,"waterboy.db",null,1);
        this.onCreate(this.getWritableDatabase());
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS "+this.table+" ("
                + "pk "+"INTEGER PRIMARY KEY NOT NULL,"
                + "name "+"VARCHAR(25),"
                + "location "+"VARCHAR(25),"
                + "wi "+"INTEGER,"
                + "wc "+"INTEGER,"
                + "fi "+"INTEGER,"
                + "fc "+"INTEGER,"
                + "si "+"INTEGER,"
                + "sc "+"INTEGER,"
                + "description "+"VARCHAR(250))");
    }

    public void onInsert(Model model){
        model.pk = (int) super.getWritableDatabase().insert(this.table,null,model.toValues());
    }

    Model onRead(int pk){
        Cursor cursor;
        if(-1 == pk){cursor = super.getReadableDatabase().rawQuery("SELECT * FROM "+this.table+" ORDER BY pk DESC LIMIT 1", null);}
        else{cursor = super.getReadableDatabase().query(this.table
                ,new String[]{"*"}
                ,"pk = ?"
                ,new String[]{String.valueOf(pk)}
                ,null
                ,null
                ,null);
        }
        cursor.moveToFirst();
        Model record = new Model(cursor);
        cursor.close();
        return record;
    }

    public ArrayList<Model> onRead(String selection, String[] args){
        Cursor cursor = super.getReadableDatabase().query(this.table
                ,new String[]{"*"}
                ,selection      // columns WHERE
                ,args           // values WHERE
                ,null
                ,null
                ,null);
        ArrayList<Model> records = new ArrayList<>();
        while(cursor.moveToNext()){records.add(new Model(cursor));}
        cursor.close();
        return records;
    }

    void onUpdate(Model model){super.getWritableDatabase().update(this.table, model.toValues(), "pk = ?", new String[]{String.valueOf(model.pk)});}

    public void onDelete(int pk){super.getWritableDatabase().delete(this.table,"pk = ?",new String[]{String.valueOf(pk)});}

    @Override
    public void onUpgrade(SQLiteDatabase db, int _old, int _new){
        db.execSQL("DROP TABLE IF EXISTS " + this.table);
        this.onCreate(db);
    }

}
