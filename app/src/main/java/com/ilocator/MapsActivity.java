package com.ilocator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;


public class MapsActivity extends AppCompatActivity implements UserLocationObjectListener {

    private UsersPresenter presenter;
    public MapView mapView;
    public final String MAPKIT_API_KEY = "62962b55-2d8b-4014-afec-85c06925b904";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_maps);
        init();

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
        presenter.cameraUserPosition();// FirebaseAuth.getInstance().signOut();
            }
        });
        presenter.onMapReady();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        presenter.unsubscribeToLocationUpdate();
        presenter.startWorker();
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
    public void onObjectRemoved(UserLocationView view) {}
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {

    }

}


