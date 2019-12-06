package com.ilocator.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ilocator.R;
import com.ilocator.activities.MainActivity;
import com.ilocator.activities.UsersActivity;
import com.ilocator.fragmnets.MapsFragment;
import com.ilocator.models.UsersModel;
import com.ilocator.services.workerClass;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.user_location.UserLocationView;

import java.util.concurrent.TimeUnit;

public class UsersPresenter {
    private static int RC_SIGN_IN = 100;
    private UsersActivity view;
    private MainActivity view_main;
    private MapsFragment view_map_fragment;
    private final UsersModel model;
    public Activity activity;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivity";
   public MapView mapView;
    private UserLocationLayer userLocationLayer;
    private Point routeStartLocation = new Point(0.0, 0.0);
    public static final int COMFORTABLE_ZOOM_LEVEL = 18;
    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 0;
    private static final double MINIMAL_DISTANCE = 50;
    private static final boolean USE_IN_BACKGROUND = false;
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;
    public DatabaseReference mDatabase;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public UsersPresenter(UsersModel model, Activity activity) {
        this.model = model;
        this.activity=activity;
    }

    public void attachView(UsersActivity usersActivity) {
        view = usersActivity;
    }

    public void attachViewMain(MainActivity mainActivity) {
        view_main = mainActivity;
    }

    public void attachViewMapFragment(MapsFragment mapsFragment) {
        view_map_fragment = mapsFragment;
    }


    public void auth(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(view, gso);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_auth]
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
       view.startActivityForResult(signInIntent, RC_SIGN_IN);

    }



    public void checkUser (){
        if (user == null) {
            Intent intent = new Intent(view_main, UsersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            view_main.startActivity(intent);
            view_main.finish(); // call this to finish the current activity
        }
        else {    view_main.showToast(user.getDisplayName());
        }
    }

    public void writeNewUser() {


      //  String point = (myLocation.getLatitude()+"+"+ myLocation.getLongitude());
        model.addNewUser();
       // mDatabase.child("users").child(userId).child(("location")).push().setValue(point);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //  showProgressDialog();
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser();
                          //  view.showToast(user.getDisplayName());
                             view.ChangeActivity ();
                            //view.updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //  Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    public  void onMapReady (final MapView mapView ){
       // mapView = this.activity.findViewById(R.id.mapview);
        mapView.getMap().setRotateGesturesEnabled(true);
        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
       // userLocationLayer.setObjectListener(this);
        locationManager = MapKitFactory.getInstance().createLocationManager();
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (myLocation == null) {
                    moveCamera(location.getPosition(), COMFORTABLE_ZOOM_LEVEL);
                }
                myLocation = location.getPosition();
            }

            private void moveCamera(Point point, float zoom) {
                mapView.getMap().move(
                        new CameraPosition(point, zoom, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
            }
        };
    }

    public void setAnchor(UserLocationView userLocationView){
        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83))
        );
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE);
    }

    public void cameraUserPosition(MapView mapView){

        view_main.showToast("Координаты "+myLocation.getLatitude()+" + "+myLocation.getLongitude());
        if(userLocationLayer.cameraPosition() != null){
            routeStartLocation = userLocationLayer.cameraPosition().getTarget();
            mapView.getMap().move(new CameraPosition(routeStartLocation,COMFORTABLE_ZOOM_LEVEL,0,0), new Animation(Animation.Type.SMOOTH,2),null);
        } else
        {
            //  mapView.getMap().move(new CameraPosition(new Point(0, 0), 15, 0, 0));
            //   mapView.getMap().move(new CameraPosition(routeStartLocation, 15, 0, 0));
        }
    }

    public void unsubscribeToLocationUpdate (){
            locationManager.unsubscribe(myLocationListener);
        }

    public void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
    }

    public void startWorker () {
        PeriodicWorkRequest gps =
                new PeriodicWorkRequest.Builder(workerClass.class, 15, TimeUnit.MINUTES )
                        .build();
        if (user != null) {
             WorkManager.getInstance(view_main).enqueue(gps);
            // OneTimeWorkRequest gps = new OneTimeWorkRequest.Builder(workerClass.class).build();
          //  WorkManager.getInstance(view_main).enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, gps);
            Log.d("START", "WORK START");
        }
    }








}


