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



    public void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public static String getTimeDate(long timestamp){
        try{
            DateFormat dateFormat = getDateTimeInstance();
            Date netDate = (new Date(timestamp));
            return dateFormat.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }


    public void writeLocation() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId =  user.getUid();
        String point = (myLocation.getLatitude()+"+"+ myLocation.getLongitude());


        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");




        HashMap<String, Object> value = new HashMap<>();
        value.put("point", point);
        value.put("timestamp", ServerValue.TIMESTAMP);
        value.put("date",   sfd.format(new Date(System.currentTimeMillis())));


        mDatabase.child("users").child(userId).child(("location")).push().setValue(value);
    }




    public void initializeLocationManager(){
        locationManager = MapKitFactory.getInstance().createLocationManager();
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(Location location) {

                myLocation = location.getPosition();
                Log.e(TAG, "onLocationChanged: " + myLocation.getLatitude()+" + "+myLocation.getLongitude());
                  writeLocation();

                //view_map.showToast(location.getPosition().getLatitude()+" + "+location.getPosition().getLongitude());
                Log.e(TAG, "STOPPED: ");
                stopSelf();
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
            }

        };



    }


    public void unsubscribeToLocationUpdate (){
        locationManager.unsubscribe(myLocationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        initializeLocationManager();
        Log.e(TAG, "onCreate");





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startMyOwnForeground();
            subscribeToLocationUpdate();
         }
        else
            startForeground(1, new Notification());





        return START_STICKY;
    }


    public void serviceMessageStart () {

        Intent alarmIntent = new Intent(gpsService.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(gpsService.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (pendingIntent != null) {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
          //  manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+(1*60*1000) , pendingIntent);
          //  manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis()+2000 , pendingIntent);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +3000, 5*60*1000, pendingIntent);

        }
    }



    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");
        serviceMessageStart();

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
        unsubscribeToLocationUpdate();
    }


}