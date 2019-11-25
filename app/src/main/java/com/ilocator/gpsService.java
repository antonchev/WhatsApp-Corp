package com.ilocator;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateTimeInstance;


public class gpsService extends Service {
    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public DatabaseReference mDatabase;
    int counter = 0;
    public static PendingIntent pendingIntent = null;
    private LocationListener myLocationListener;
    private Point myLocation;
    private com.yandex.mapkit.location.LocationManager locationManager;
    public static final int COMFORTABLE_ZOOM_LEVEL = 18;

    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 0;
    private static final double MINIMAL_DISTANCE = 50;
    private static final boolean USE_IN_BACKGROUND = false;
    int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

    public UsersPresenter presenter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private MapsActivity view_map;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }















    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
     //   serviceMessageStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startMyOwnForeground();
           // presenter.startWorker();
          //
            final UsersModel usersModel = new UsersModel();
            presenter = new UsersPresenter(usersModel,view_map);
            presenter.attachViewMaps(view_map);
         //   PeriodicWorkRequest gps =
               //    new PeriodicWorkRequest.Builder(workerClass.class, 15, TimeUnit.MINUTES )
               //             .build();
            OneTimeWorkRequest gps = new OneTimeWorkRequest.Builder(workerClass.class).build();
            if (user != null) {

             //   WorkManager.getInstance(view_map).enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE,gps);
               WorkManager.getInstance(view_map).enqueue(gps);
               if ( workerClass.Result.success() != null) {
                   stopSelf();
               }

                //  WorkManager.getInstance(view_map).enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, gps);
                Log.d("START", "WORK START");
            }



        }
        else
            startForeground(1, new Notification());
        return START_STICKY;
    }


    public void serviceMessageStart () {




        if (pendingIntent != null) {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
              manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(15*60*1000) , pendingIntent);
            //  manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis()+2000 , pendingIntent);
          //  manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 17*60*1000, pendingIntent);

        }
     //   Intent alarmIntent = new Intent(gpsService.this, AlarmReceiver.class);
       // pendingIntent = PendingIntent.getBroadcast(gpsService.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    }



    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.ilocator_1";
        String channelName = "Фоновый процес";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("iLocator GPS")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

    }


}