package com.ilocator.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class AlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {


        Intent intent_service = new Intent(context, gpsService.class);
        context.startForegroundService(intent_service);
        // Выполняем свои действия
    }
}
