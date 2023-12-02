package com.srinikethandev.rtcv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomConfig;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomFragment;

import java.util.HashMap;
import java.util.Map;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.ChannelMediaOptions;


public class clientjoin extends AppCompatActivity {

    /*
    // Fill the App ID of your project generated on Agora Console.
    private final String appId = "5e04ff9d2c25477680db3d565921240f";
    // Fill the channel name.
    private String channelName = "Test123@";
    // Fill the temp token generated on Agora Console.
    private String token = "007eJxTYNjuE/x/n+rVqw7Hs7893fiKRcT3vufhU2KfxGb3MvQFJn9RYDBNNTBJS7NMMUo2MjUxNzezMEhJMk4xNTO1NDI0MjFIe89omNIQyMjA2rmYgREKQXwOhpDU4hJDI2MHBgYARYQgtQ==";
    // An integer that identifies the local user.
    private int uid = 0;
    // Track the status of your connection
    private boolean isJoined = false;

    // Agora engine instance
    private RtcEngine agoraEngine;
    // UI elements
    private TextView infoText;
    private Button joinLeaveButton;


    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO
            };

    private boolean checkSelfPermission()
    {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) !=  PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setupVoiceSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(()->infoText.setText("Remote user joined: " + uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            // Successfully joined a channel
            isJoined = true;
            showMessage("Joined Channel " + channel);
            runOnUiThread(()->infoText.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline " + uid + " " + reason);
            if (isJoined) runOnUiThread(()->infoText.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onLeaveChannel(RtcStats 	stats) {
            // Listen for the local user leaving the channel
            runOnUiThread(()->infoText.setText("Press the button to join a channel"));
            isJoined = false;
        }
    };

    private void joinChannel() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.autoSubscribeAudio = true;
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine.joinChannel(token, channelName, uid, options);
    }


    public void joinLeaveChannel(View view) {
        if (isJoined) {
            agoraEngine.leaveChannel();
            joinLeaveButton.setText("Join");
        } else {
            joinChannel();
            joinLeaveButton.setText("Leave");
        }
    }

    */

    private Button joinroom;
    EditText roomname;
    EditText qname;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientjoin);


        db = FirebaseFirestore.getInstance();
        joinroom = (Button) findViewById(R.id.cliroomjoin);
        roomname = findViewById(R.id.roomname);
        qname = findViewById(R.id.qname);
        joinroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String RoomName = roomname.getText().toString();
                Map<String,Object> user = new HashMap<>();


                user.put("Status","Success");

                db.collection("test").document(RoomName).set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(clientjoin.this,"Successful",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(clientjoin.this, "Failed", Toast.LENGTH_SHORT).show();

                            }
                        });


                openclilobby();
            }
        });


    }



    public void openclilobby(){

        Intent intent = new Intent(this, clientlobby.class);
        intent.putExtra("roomname",roomname.getText().toString());
        intent.putExtra("qname",qname.getText().toString());
        startActivity(intent);

    }

}