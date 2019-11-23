package com.ilocator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;

import java.util.concurrent.TimeUnit;


public class MapsActivity extends AppCompatActivity implements UserLocationObjectListener {

    private UsersPresenter presenter;
    public MapView mapView;
    public final String MAPKIT_API_KEY = "62962b55-2d8b-4014-afec-85c06925b904";


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_maps);
        init();


      //
        }

    private void init(){
        mapView = findViewById(R.id.mapview);
        // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        final UsersModel usersModel = new UsersModel();
        presenter = new UsersPresenter(usersModel,this);
        presenter.attachViewMaps(this);
        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presenter.cameraUserPosition();
            //    presenter.writeNewUser();
                // FirebaseAuth.getInstance().signOut();
            }
        });
        presenter.onMapReady();
      //  presenter.writeNewUser();

    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        presenter.unsubscribeToLocationUpdate();


    //    Intent intent = new Intent(this, gpsService.class);
     //   startService(intent);

       // OneTimeWorkRequest gps = new OneTimeWorkRequest.Builder(workerClass.class).build();

        PeriodicWorkRequest gps =
                new PeriodicWorkRequest.Builder(workerClass.class, 15, TimeUnit.MINUTES )
                                             .build();




        if (user != null) {
            //    startForegroundService(intent);
           // WorkManager.getInstance(this).enqueue(gps);
            WorkManager.getInstance(this).enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, gps);
            Log.d("START", "WORK START");
        }

        super.onStop();


    }








    public void showToast(String resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        presenter.checkUser();
        presenter.subscribeToLocationUpdate();
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {

      presenter.setAnchor(userLocationView);

       // userLocationView.getArrow().setIcon(ImageProvider.fromResource(
         //       this, R.drawable.user_arrow));





    }

    public void onObjectRemoved(UserLocationView view) {
    }
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {

    }

}


