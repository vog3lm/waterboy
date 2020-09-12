package de.sit.waterboy.application;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Model implements Serializable {

    int pk;

    String name = "";
    String location = "";
    String description = "";

    int wi = 7;
    int wc = 7;
    int fi = 90;
    int fc = 90;
    int si = 365;
    int sc = 365;

    Model(){}

    public Model(String[] record){
        this.name = record[0];
        this.location = record[1];
        this.wi = Integer.parseInt(record[2]);
        this.wc = Integer.parseInt(record[3]);
        this.fi = Integer.parseInt(record[4]);
        this.fc = Integer.parseInt(record[5]);
        this.si = Integer.parseInt(record[6]);
        this.sc = Integer.parseInt(record[7]);
        this.description = record[8];
    }

    Model(Cursor cursor){
        this.pk = cursor.getInt(0);
        this.name = cursor.getString(1);
        this.location = cursor.getString(2);
        this.wi = cursor.getInt(3);
        this.wc = cursor.getInt(4);
        this.fi = cursor.getInt(5);
        this.fc = cursor.getInt(6);
        this.si = cursor.getInt(7);
        this.sc = cursor.getInt(8);
        this.description = cursor.getString(9);
    }

    public String[] toArray(){
        return new String[]{this.name,this.location,String.valueOf(this.wi),String.valueOf(this.wc),String.valueOf(this.fi),String.valueOf(this.fc),String.valueOf(this.si),String.valueOf(this.sc),this.description};
    }

    ContentValues toValues(){
        ContentValues values = new ContentValues();
        values.put("name",this.name);
        values.put("location",this.location);
        values.put("wi",this.wi);
        values.put("wc",this.wc);
        values.put("fi",this.fi);
        values.put("fc",this.fc);
        values.put("si",this.si);
        values.put("sc",this.sc);
        values.put("description",this.description);
        return values;
    };
    @NonNull
    @Override
    public String toString(){
        return "pk:"+this.pk
             +";name:"+this.name
             +";location:"+this.name
             +";wi:"+this.name +";wc:"+this.name
             +";fi:"+this.name +";fc:"+this.name
             +";si:"+this.name +";sc:"+this.name
             +";description:"+this.description;
    };
}
