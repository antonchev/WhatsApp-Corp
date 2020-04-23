package com.ilocator.fragmnets;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ilocator.R;
import com.ilocator.activities.ChatRoomActivity;
import com.ilocator.activities.MainActivity;
import com.ilocator.models.ChatRoom;
import com.ilocator.utils.ChatRoomsAdapter;
import com.ilocator.utils.MyApplication;
import com.ilocator.utils.SimpleDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.yandex.runtime.Runtime.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView loading;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = "Chat Room";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String[] myDataset = {"Создать группу","Создать группу"};
    public SettingsFragment() {
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
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

        TextView loading;
        fetchChatRooms();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter


                handlePushNotification(intent);
            }};

    }

    @Override
    public void onStart() {
        super.onStart();



    }
    public static final String PUSH_NOTIFICATION = "pushNotification";
    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));

    }



    public void handlePushNotification(Intent intent) {

        String to_phone = intent.getStringExtra("to_phone");
        info.androidhive.gcm.model.Message message = (info.androidhive.gcm.model.Message) intent.getSerializableExtra("msg_text");


        String msg_text = message.getMessage();
        String timestamp = message.getCreatedAt();


            updateRow(to_phone, msg_text,timestamp);

    }
    public void updateRow(String chatRoomId, String message, String timestamp) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message);
                cr.setTimestamp(timestamp);
                cr.setUnreadCount(cr.getUnreadCount() + 1);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }



    private void fetchChatRooms() {

        String url = "https://"+MyApplication.getInstance().getPrefManager().getHost()+"/api/wa_dialogs_get ";

        StringRequest strReq = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getString("err") == "false") {
                        JSONArray chatRoomsArray = obj.getJSONArray("data");
                        for (int i = 0; i < chatRoomsArray.length(); i++) {
                            JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                            ChatRoom cr = new ChatRoom();
                            cr.setId(chatRoomsObj.getString("to_phone"));
                            cr.setName(chatRoomsObj.getString("c_name"));

                            cr.setLastMessage(chatRoomsObj.getString("msg_last"));

                           // cr.setUnreadCount(chatRoomsObj.getInt("from_me_last"));
                            cr.setTimestamp(chatRoomsObj.getString("dt"));

                            chatRoomArrayList.add(cr);

                        }

                    } else  {
                                              MyApplication.getInstance().logout();

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                  //  Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                loading.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();

                // subscribing to all chat room topics

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", MyApplication.getInstance().getPrefManager().getUser().getToken());


                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };


        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);


    }



    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  MapView mapView = (MapView) inflater.inflate(R.layout., null);
        // Inflate the layout for this fragment
        Log.v("TOKEN",  MyApplication.getInstance().getPrefManager().getUser().getToken());

        View FragmentSettingView = inflater.inflate(R.layout.fragment_settings, container, false);









        recyclerView =  FragmentSettingView.findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        loading =  FragmentSettingView.findViewById(R.id.wait);





        chatRoomArrayList = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(getContext(), chatRoomArrayList);

      //  MyAdapter.MyViewHolder my = new MyAdapter.MyViewHolder(FragmentSettingView);



        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getContext(), recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                ChatRoom chatRoom = chatRoomArrayList.get(position);
                Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return FragmentSettingView;



    }







    @Override
    public void onDetach() {
        super.onDetach();

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
