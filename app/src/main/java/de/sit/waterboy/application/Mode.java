package de.sit.waterboy.application;

import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import de.sit.waterboy.R;

abstract class Mode {

    private boolean isFiltered = false;

    Listing listing;
    Menu menu;
    Store store;

    Mode(Listing listing, Menu menu, Store store){
        this.listing = listing;
        this.menu = menu;
        this.store = store;
    }

    abstract void onCreate();
    abstract void onPrimary();
    void onSecondary(){onFilter();}
    abstract void onClick(int tag);
    abstract void onLong(int tag);
    abstract void onBack();
    private void onFilter(){
        if(this.isFiltered){
            LinearLayout listing = this.listing.findViewById(R.id.listing);
            for(int i=0; i<listing.getChildCount()-1; i++){listing.getChildAt(i).setVisibility(View.VISIBLE);}
        }else{this.onHide();}
        this.isFiltered = !this.isFiltered;
    };
    protected abstract void onHide();
}
