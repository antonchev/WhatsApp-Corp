package com.ilocator.fragmnets;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.ilocator.R;
import com.ilocator.UsersModel;
import com.ilocator.UsersPresenter;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;

import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment  implements UserLocationObjectListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public MapView mapView;
    public final String MAPKIT_API_KEY = "62962b55-2d8b-4014-afec-85c06925b904";
    private UsersPresenter presenter;
    private UserLocationLayer userLocationLayer;
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;
    public DatabaseReference mDatabase;
    public static final int COMFORTABLE_ZOOM_LEVEL = 18;
    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 0;
    private static final double MINIMAL_DISTANCE = 50;
    private static final boolean USE_IN_BACKGROUND = false;
    private Point routeStartLocation = new Point(0.0, 0.0);


    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public  void onMapReady (final MapView mapView ){


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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public void onStart() {
        super.onStart();
            MapKitFactory.getInstance().onStart();
          mapView.onStart();
       subscribeToLocationUpdate();

    }

    public void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
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

    @Override
    public void onStop() {
        super.onStop();
         mapView.onStop();
           MapKitFactory.getInstance().onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  MapView mapView = (MapView) inflater.inflate(R.layout., null);
        // Inflate the layout for this fragment
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(getActivity().getApplicationContext());

        View FragmentMapView = inflater.inflate(R.layout.fragment_maps, container, false);

        mapView = FragmentMapView.findViewById(R.id.mapview_xml);

      //  MapView mapView = FragmentMapView.findViewById(R.id.mapview);


        onMapReady(mapView);

         FragmentMapView.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
         cameraUserPosition();// FirebaseAuth.getInstance().signOut();
              }
           });


        return FragmentMapView;



    }

    public void cameraUserPosition(){

       showToast("Координаты "+myLocation.getLatitude()+" + "+myLocation.getLongitude());
        if(userLocationLayer.cameraPosition() != null){
            routeStartLocation = userLocationLayer.cameraPosition().getTarget();
            mapView.getMap().move(new CameraPosition(routeStartLocation,COMFORTABLE_ZOOM_LEVEL,0,0), new Animation(Animation.Type.SMOOTH,2),null);
        } else
        {
            //  mapView.getMap().move(new CameraPosition(new Point(0, 0), 15, 0, 0));
            //   mapView.getMap().move(new CameraPosition(routeStartLocation, 15, 0, 0));
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void showToast(String resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
