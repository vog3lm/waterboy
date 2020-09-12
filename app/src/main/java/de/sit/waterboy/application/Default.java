package de.sit.waterboy.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.sit.waterboy.common.DialogDeletable;
import de.sit.waterboy.common.Dialogs;
import de.sit.waterboy.common.Properties;
import de.sit.waterboy.R;

class Default extends Mode implements DialogDeletable {

    Default(Listing listing, Menu menu, Store store) {
        super(listing, menu, store);
        this.onCreate();
    }

    Default(Mode mode){
        super(mode.listing,mode.menu,mode.store);
        this.onCreate();
    }
    @Override
    void onCreate(){
        /**/
        LinearLayout list = super.listing.findViewById(R.id.listing);
        for(int i=0; i<list.getChildCount()-1; i++){
            CheckBox box = list.getChildAt(i).findViewById(R.id.checkbox);
            box.setChecked(false);
            box.setVisibility(View.GONE);
        }
        /**/
        super.listing.findViewById(R.id.water).setVisibility(View.VISIBLE);
        super.listing.findViewById(R.id.fertilize).setVisibility(View.VISIBLE);
        super.listing.findViewById(R.id.soil).setVisibility(View.VISIBLE);
        FloatingActionButton create = super.listing.findViewById(R.id.create);
        create.setImageResource(R.drawable.ic_leaf_white_24dp);
        /**/
        ActionBar bar = super.listing.getSupportActionBar();
        if(null != bar){bar.setTitle(R.string.app_name);}
        if(null != super.menu){
            super.menu.getItem(0).setVisible(true);
            super.menu.getItem(1).setIcon(R.drawable.ic_filter_white_24dp);
        }
        /**/
        super.listing.mode = this;
    }
    @Override
    void onPrimary(){
        Bundle extras = new Bundle();
        extras.putSerializable(Properties.REQUEST_RECORD,new Model());
        Intent intent = new Intent(super.listing, Details.class);
        intent.putExtras(extras);
        super.listing.startActivityForResult(intent, Properties.REQUEST_CREATE);
    }
    @Override
    void onClick(int pk){
        Bundle extras = new Bundle();
        extras.putSerializable(Properties.REQUEST_RECORD,super.store.onRead(pk));
        Intent intent = new Intent(super.listing, Details.class);
        intent.putExtras(extras);
        super.listing.startActivityForResult(intent, Properties.REQUEST_DETAIL);
    }
    @Override
    void onLong(int pk){ new Dialogs(super.listing).onDelete(pk,this);}
    @Override
    void onBack(){super.listing.finish();}
    @Override
    public void onDelete(int index){
        super.store.onDelete(index);
        LinearLayout list = super.listing.findViewById(R.id.listing);
        list.removeView(list.findViewWithTag(index));
    }
    @Override
    protected void onHide(){
        LinearLayout listing = this.listing.findViewById(R.id.listing);
        for(int i=0; i<listing.getChildCount()-1; i++){
            View item = listing.getChildAt(i);
            LinearLayout state = item.findViewById(R.id.state);
            if(0 == state.getChildCount()){item.setVisibility(View.GONE);}
        }
    }
}
