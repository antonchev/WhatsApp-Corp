/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ilocator.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ilocator.R;
import com.ilocator.activities.ChatRoomActivity;
import com.ilocator.activities.MainActivity;
import com.ilocator.fragmnets.SettingsFragment;
import com.ilocator.models.User;
import com.ilocator.utils.MyApplication;


import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String to_phone, msg_text;
        //  Intent intent_service = new Intent(this, gpsService.class);
        // startForegroundService(intent_service);

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.mWhat
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            //    Log.d(TAG, "Message data payload: " +  remoteMessage.getData().get("paydata"));
            String dialog = MyApplication.getInstance().getPrefManager().getRun();

            if (dialog.equals("dialogs")) {
                to_phone = remoteMessage.getData().get("to_phone");
                msg_text = remoteMessage.getData().get("msg_text");
                String dt_ins = remoteMessage.getData().get("dt_ins");
                String cid = remoteMessage.getData().get("cid");

                processChatRoomPush(to_phone, msg_text, 0, cid, dt_ins, null);
                Log.d(TAG, "Диалоги");
                Log.d(TAG, "Data" + to_phone + msg_text);

            } else if (dialog.equals("message")) {
                Log.d(TAG, "Сообщения");

                to_phone = remoteMessage.getData().get("to_phone");
                msg_text = remoteMessage.getData().get("msg_text");
                String dt_ins = remoteMessage.getData().get("dt_ins");
                String from_me = remoteMessage.getData().get("from_me");
                String cid = remoteMessage.getData().get("cid");
                String u_name = remoteMessage.getData().get("u_name");

                if (from_me.equals("0")) {
                    processChatRoomPush(to_phone, msg_text, 0, cid, dt_ins, null);

                } else if (from_me.equals("1")) {
                    processChatRoomPush(to_phone, msg_text, 1, cid, dt_ins, u_name);
                }
            } else {
                to_phone = remoteMessage.getData().get("to_phone");
                msg_text = remoteMessage.getData().get("msg_text");
                String dt_ins = remoteMessage.getData().get("dt_ins");
                String cid = remoteMessage.getData().get("cid");
                sendNotification(to_phone, msg_text);
                processChatRoomPush(to_phone, msg_text, 0, cid, dt_ins, null);
                Log.d(TAG, "Не запущено");
            }

        }
    }





        // Check if message contains a notification payload.
  //      if (remoteMessage.getNotification() != null) {
       //     Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody()+remoteMessage.getData());
       //     sendNotification(remoteMessage.getNotification().getBody());
      //  }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    // [END receive_message]
    public static final String PUSH_NOTIFICATION = "pushNotification";
    private void processChatRoomPush(String phone, String msg_text, int from_me, String cid, String dt_ins, String Author) {

        User user = new User(MyApplication.getInstance().getPrefManager().getUser().getId(), MyApplication.getInstance().getPrefManager().getUser().getName(), null,null);

        info.androidhive.gcm.model.Message message = new info.androidhive.gcm.model.Message();
        message.setId(cid);
        message.setMessage(msg_text);
        message.setCreatedAt(dt_ins);
        message.setUser(user);
        message.setFrom_me(from_me);
        if (Author!=null)
        message.setAuthor(Author);

        Intent pushNotification = new Intent(PUSH_NOTIFICATION);
        pushNotification.putExtra("to_phone", phone);
        pushNotification.putExtra("msg_text", message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }
    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(workerClass.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     *
     */


    private void sendNotification(String title,String messageBody) {



        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("chat_room_id",title);
        intent.putExtra("name",title);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "1")
                        .setSmallIcon(R.drawable.ic_music_and_multimedia)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(getBaseContext(), R.color.red))
                        .setVibrate(new long[]{0, 10, 100, 10})
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.

        int id = createID();

        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

    public int createID(){
        Date now = new Date();

        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
}
