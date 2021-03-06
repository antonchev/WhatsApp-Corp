package com.ilocator.fragmnets;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ilocator.R;
import com.ilocator.activities.MainActivity;
import com.ilocator.presenters.UsersPresenter;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;


public class MapsFragment extends Fragment  {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    public MapView mapView;
    public final String MAPKIT_API_KEY = "62962b55-2d8b-4014-afec-85c06925b904";
    private UsersPresenter presenter;


    public MapsFragment() {
        // Required empty public constructor
    }


    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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



    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  MapView mapView = (MapView) inflater.inflate(R.layout., null);
        // Inflate the layout for this fragment


        View FragmentMapView = inflater.inflate(R.layout.fragment_maps, container, false);



      //  MapView mapView = FragmentMapView.findViewById(R.id.mapview);




         FragmentMapView.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
               // ((MainActivity)getActivity()).checkuser();

              }
           });


        return FragmentMapView;



    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
