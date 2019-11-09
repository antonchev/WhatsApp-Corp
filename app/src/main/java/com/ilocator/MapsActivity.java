package com.ilocator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.Animation;

import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.logo.Alignment;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;
import androidx.core.content.ContextCompat;


public class MapsActivity extends AppCompatActivity implements UserLocationObjectListener {

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private Point routeStartLocation = new Point(0.0, 0.0);
    private boolean followUserLocation = false;
    private boolean permissionLocation =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey("065d417d-f54b-479d-aee2-f7aba805f889");
        MapKitFactory.initialize(this);
     // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUserPosition();
               // FirebaseAuth.getInstance().signOut();
            }
        });

        onMapReady();

    }

    private  void onMapReady (){

        mapView = findViewById(R.id.mapview);

        mapView.getMap().setRotateGesturesEnabled(true);

        MapKit mapKit = MapKitFactory.getInstance();

        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        //  userLocationLayer.setObjectListener(this);

        cameraUserPosition();

    }




   public void cameraUserPosition(){



        if(userLocationLayer.cameraPosition() != null){
            routeStartLocation = userLocationLayer.cameraPosition().getTarget();
            mapView.getMap().move(new CameraPosition(routeStartLocation,15,0,0), new Animation(Animation.Type.SMOOTH,2),null);
        } else
        {

            mapView.getMap().move(new CameraPosition(new Point(0, 0), 15, 0, 0));
         //   mapView.getMap().move(new CameraPosition(routeStartLocation, 15, 0, 0));


        }
   }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {



            Intent intent = new Intent(this, UsersActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // call this to finish the current activity
        }
        else {    showToast(user.getDisplayName()); }


    }

   private void setAnchor(UserLocationView userLocationView){
       userLocationLayer.setAnchor(

               new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
               new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83))


       );
       userLocationView.getAccuracyCircle().setFillColor(Color.BLUE);



   }


    @Override
    public void onObjectAdded(UserLocationView userLocationView) {

      setAnchor(userLocationView);

       // userLocationView.getArrow().setIcon(ImageProvider.fromResource(
         //       this, R.drawable.user_arrow));





    }


    public void onObjectRemoved(UserLocationView view) {
    }


    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {


    }



}


