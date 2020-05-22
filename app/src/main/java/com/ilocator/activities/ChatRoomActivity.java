package com.ilocator.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ilocator.R;
import com.ilocator.utils.ChatMsgAdapter;

import com.ilocator.utils.MyApplication;

import info.androidhive.gcm.model.Message;
import com.ilocator.models.User;
import com.ilocator.utils.SpeedyLinearLayoutManager;


public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private String chatRoomId;
    private RecyclerView recyclerView;
    private ChatMsgAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private TextView loading;
    private Button btnSend;
    String chatRoomId_loc;
    public static final String PUSH_NOTIFICATION = "pushNotification";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (Button) findViewById(R.id.btn_send);

        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        String title = intent.getStringExtra("name");

        getSupportActionBar().setTitle(title);
     //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (chatRoomId == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        // self user id is to identify the message owner
        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new ChatMsgAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new SpeedyLinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loading = findViewById(R.id.wait);

        fetchChatThread();

        MyApplication.getInstance().getPrefManager().storeRun("messages");
    }

    @Override
    public void onBackPressed() {

            //  viewPager.setCurrentItem(viewPager.getCurrentItem() ,false);
        Intent pushNotification = new Intent(PUSH_NOTIFICATION);
        pushNotification.putExtra("unread", "0");
        pushNotification.putExtra("to_phone", chatRoomId_loc);


        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            finish();



    }



    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));


    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        MyApplication.getInstance().getPrefManager().storeRun("0");
        super.onPause();
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("msg_text");
        chatRoomId_loc = intent.getStringExtra("to_phone");
        if (chatRoomId.equals(chatRoomId_loc)){
        if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }
        }
    }

    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     * */




    private void sendMessage() {
        final String messageIN = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(messageIN)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        this.inputMessage.setText("");

        String url = "https://"+MyApplication.getInstance().getPrefManager().getHost()+"/api/wa_msg_put";

        StringRequest strReq = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("err") == false) {
                        JSONObject commentObj = obj.getJSONObject("data");

                        String commentId = commentObj.getString("last_id");



                        User user = new User(MyApplication.getInstance().getPrefManager().getUser().getId(), MyApplication.getInstance().getPrefManager().getUser().getName(), null,null);

                        Message message = new Message();
                        message.setId(commentId);
                        message.setMessage(messageIN);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        String format = simpleDateFormat.format(new Date());

                        message.setCreatedAt(format);
                        message.setUser(user);
                        message.setFrom_me(1);
                        message.setAuthor(user.getName());

                        messageArrayList.add(message);

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            // scrolling to bottom of the recycler view
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                inputMessage.setText(messageIN);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", MyApplication.getInstance().getPrefManager().getUser().getToken());
                params.put("text", messageIN);
                params.put("phone", chatRoomId);

                Log.e(TAG, "Params: " + params.toString());

                return params;
            };
        };


        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }



    /**
     * Fetching all the messages of a single chat room
     * */
    private void fetchChatThread() {

        String url = "https://"+MyApplication.getInstance().getPrefManager().getHost()+"/api/wa_msg_get";

        StringRequest strReq = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("err") == false) {
                        JSONArray commentsObj = obj.getJSONArray("data");

                        for (int i = 0; i < commentsObj.length(); i++) {
                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String commentId = chatRoomId;
                            int from_me = commentObj.getInt("from_me");
                            String commentText = commentObj.getString("msg_text");
                            String createdAt = commentObj.getString("dt_ins");
                            String author = commentObj.getString("u_name");

                            String userId = chatRoomId;
                            String userName = chatRoomId;
                            User user = new User(userId, userName, null,null);

                            Message message = new Message();


                            if (commentText.length()>12) {

                                int start = 0;
                                int end = 12;
                                char[] dst = new char[end - start];

                                String commentText2 = commentText;
                                commentText2.getChars(start, end, dst, 0);

                                String dst2 = new String(dst);

                                if (dst2.equals("https://fire"))
                                {

                                   message.setImage(commentText);

                                    message.setId(commentId);

                                    message.setCreatedAt(createdAt);
                                    message.setUser(user);
                                    message.setFrom_me(from_me);
                                    message.setAuthor(author);

                                }
                                else
                                    {
                                        message.setId(commentId);
                                        message.setMessage(commentText);
                                        message.setCreatedAt(createdAt);
                                        message.setUser(user);
                                        message.setFrom_me(from_me);
                                        message.setAuthor(author);
                                    }
                            } else {

                                message.setId(commentId);
                                message.setMessage(commentText);
                                message.setCreatedAt(createdAt);
                                message.setUser(user);
                                message.setFrom_me(from_me);
                                message.setAuthor(author);
                            }
                          //  message.setImage("https://wallpapersite.com/images/pages/pic_w/14693.jpg");


                            messageArrayList.add(message);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }
                        loading.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {  @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("token", MyApplication.getInstance().getPrefManager().getUser().getToken());
            params.put("phone", chatRoomId );


            Log.e(TAG, "params: " + params.toString());
            return params;
        }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    @Override
    protected void onStop() {
        // mapView.onStop();
        //   MapKitFactory.getInstance().onStop();
        // presenter.unsubscribeToLocationUpdate();
        // presenter.startWorker();


        // startForegroundService(intent_service);

       // MyApplication.getInstance().getPrefManager().storeRun("0");
        super.onStop();
    }



    @Override
    protected void onStart() {
        super.onStart();
        //    MapKitFactory.getInstance().onStart();
        //  mapView.onStart();

        //  presenter.subscribeToLocationUpdate();

        MyApplication.getInstance().getPrefManager().storeRun("message");



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //    MapKitFactory.getInstance().onStart();
        //  mapView.onStart();

        //  presenter.subscribeToLocationUpdate();

       // MyApplication.getInstance().getPrefManager().storeRun("0");


    }




}
