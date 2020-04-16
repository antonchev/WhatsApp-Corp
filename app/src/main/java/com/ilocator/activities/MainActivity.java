package com.ilocator.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ilocator.utils.CustomViewPager;
import com.ilocator.R;
import com.ilocator.models.User;
import com.ilocator.presenters.UsersPresenter;
import com.ilocator.utils.ViewPagerAdapter;
import com.ilocator.fragmnets.GroupsFragment;
import com.ilocator.fragmnets.MapsFragment;
import com.ilocator.fragmnets.SettingsFragment;
import com.ilocator.services.gpsService;


public class MainActivity extends AppCompatActivity{

    private UsersPresenter presenter;
    BottomNavigationView bottomNavigation;

    CustomViewPager viewPager;
    ViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
     //   MapKitFactory.setApiKey(MAPKIT_API_KEY);
    //    MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        init();



        }


    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1,false);
        }else if (viewPager.getCurrentItem() == 2) {
          //  viewPager.setCurrentItem(viewPager.getCurrentItem() ,false);
            finish();
        }else finish();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_profile:
                viewPager.setCurrentItem(1);
                return true;
           // case R.id.help:
           //     showHelp();
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    private void init() {
      //  mapView = findViewById(R.id.mapview);
        // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        final User usersModel = new User();
        presenter = new UsersPresenter(usersModel, this);
        presenter.attachViewMain(this);


       // bottomNavigation = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewpager1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);


        adapter = new ViewPagerAdapter(MainActivity.this.getSupportFragmentManager());
        adapter.addFragment(new MapsFragment(), "Maps");
        adapter.addFragment(new GroupsFragment(), "Groups");
        adapter.addFragment(new SettingsFragment(), "Settings");
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);

        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {


                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_map:

                                viewPager.setCurrentItem(0);

                                return true;
                            case R.id.navigation_groups:

                                viewPager.setCurrentItem(1);
                                return true;
                            case R.id.navigation_settings:
                                viewPager.setCurrentItem(2);
                                return true;
                        }
                        return false;
                    }


                };

       // openFragment(MapsFragment.newInstance("",""));

       // bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }



    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
       // mapView.onStop();
     //   MapKitFactory.getInstance().onStop();
       // presenter.unsubscribeToLocationUpdate();
       // presenter.startWorker();

        Intent intent_service = new Intent(this, gpsService.class);
       // startForegroundService(intent_service);


        super.onStop();
    }

    public void showToast(String resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    //    MapKitFactory.getInstance().onStart();
      //  mapView.onStart();
        presenter.checkUser();
      //  presenter.subscribeToLocationUpdate();





    }

    protected void Firebase (){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FIREBASE", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("MESSAGE", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }



}


