package de.sit.waterboy.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import java.util.Calendar;

import de.sit.waterboy.application.Receiver;

public class Properties {

    public static final int REQUEST_CREATE = 100;
    public static final int REQUEST_DETAIL = 101;
    public static final int REQUEST_UPDATE = 102;
    public static final int REQUEST_DELETE = 103;
    public static final int REQUEST_REMIND = 104;
    public static final int REQUEST_SNOOZE = 105;
    public static final int REQUEST_CONFIG = 106;
    public static final int REQUEST_IMPORT = 107;

    public static final String REQUEST_CODE = "code";
    public static final String REQUEST_RECORD = "get";


    public static final String IS_DEFLOWERED = "isDeflowered";
    public static final String REMINDER_FLAG = "reminderFlag";
    public static final String REMINDER_TIME = "reminderTime";
    public static final String REMINDER_REDO = "reminderSnooze";

    public static final String DEAMON_RESTART_KEY = "restartDeamon";
    public static final String COLOR_LAYOUT_KEY = "layoutColor";
    public static final int COLOR_LAYOUT = Color.parseColor("#1D1D1D");
    public static final String COLOR_NORMAL_KEY = "normalColor";
    public static final int COLOR_NORMAL = Color.parseColor("#008368");
    public static final String COLOR_DANGER_KEY = "dangerColor";
    public static final int COLOR_DANGER = Color.parseColor("#EAC200");
    public static final String COLOR_URGENT_KEY = "urgentColor";
    public static final int COLOR_URGENT = Color.parseColor("#E6155D");

    public static final String LEVEL_DANGER_KEY = "dangerLevel";
    public static final String LEVEL_DANGER = "10";


    public static final String EXPORT_KEY = "exportActivity";
    public static final String IMPORT_KEY = "importActivity";
    /* android-sdk/platform-tools/adb shell dumpsys alarm | grep de.sit.waterboy */
    public static PendingIntent getReminderIntent(Context context, boolean create){
        Intent intent = new Intent(context, Receiver.class);
        intent.putExtra(Properties.REQUEST_CODE, Properties.REQUEST_REMIND);
        if(create){return PendingIntent.getBroadcast(context, Properties.REQUEST_REMIND, intent, PendingIntent.FLAG_UPDATE_CURRENT);}
        else{return PendingIntent.getBroadcast(context, Properties.REQUEST_REMIND, intent, PendingIntent.FLAG_NO_CREATE);}
    }
    public static PendingIntent getDeamonIntent(Context context, boolean create){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent(context, Receiver.class);
        intent.putExtra(Properties.REQUEST_CODE, Properties.REQUEST_UPDATE);
        if (create){return PendingIntent.getBroadcast(context, Properties.REQUEST_UPDATE, intent, PendingIntent.FLAG_UPDATE_CURRENT);}
        else{return PendingIntent.getBroadcast(context, Properties.REQUEST_UPDATE, intent, PendingIntent.FLAG_NO_CREATE);}
    }
}
