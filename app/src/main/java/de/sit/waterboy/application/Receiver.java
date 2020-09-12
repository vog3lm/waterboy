package de.sit.waterboy.application;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import de.sit.waterboy.common.Properties;
import de.sit.waterboy.R;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Bundle extras = intent.getExtras();
        if(null == extras){return;}
        if(!extras.containsKey(Properties.REQUEST_CODE)){return;}
        int code = extras.getInt(Properties.REQUEST_CODE);
        if(Properties.REQUEST_UPDATE == code){this.onUpdate(context);}
        else if(Properties.REQUEST_REMIND == code){this.onRemind(context);}
    }

    private void onUpdate(Context context){
        Store store = new Store(context);
        for(Model model : store.onRead(null,null)){
            model.wc--;
            model.fc--;
            model.sc--;
            store.onUpdate(model);
        }
    }

    private void onRemind(Context context){
        Store store = new Store(context);
        String plants = "";
        for(Model model : store.onRead(null,null)){
            if(0 > model.wc
            || 0.1f*model.wi > model.wc
            || 0 > model.fc
            || 0.1f*model.fi > model.fc
            || 0 > model.sc
            || 0.1f*model.si > model.sc){
                plants = plants+", "+model.name;
            }
        }
        if(!"".equals(plants)){
            Intent ri = new Intent(context, Listing.class);
            ri.putExtra(Properties.REQUEST_CODE,Properties.REQUEST_REMIND);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(ri);
            PendingIntent start = stackBuilder.getPendingIntent(Properties.REQUEST_REMIND, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent si = new Intent(context, Receiver.class);
            si.putExtra(Properties.REQUEST_CODE,Properties.REQUEST_SNOOZE);
            //    si.setAction(String.valueOf(Properties.REQUEST_SNOOZE));
            //    snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
            PendingIntent snooze = PendingIntent.getBroadcast(context, Properties.REQUEST_SNOOZE, si, 0);

            String id = "my_channel_id";
            String name = "my_channel_name";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,id)
                    .setSmallIcon(R.drawable.ic_leaf_white_24dp)
                    .setContentTitle("Plant Car")
                    .setContentText(plants.substring(2)+" need some care.")
                    .setContentIntent(start)
                    .addAction(R.drawable.ic_cog_white_24dp,"snooze",snooze)
                    .setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(id,name,NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(Properties.REQUEST_REMIND, builder.build());
        }
    }
}
