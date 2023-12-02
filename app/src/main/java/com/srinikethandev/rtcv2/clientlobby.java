package com.srinikethandev.rtcv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class clientlobby extends AppCompatActivity {

    String songname,songurl;
    Button recordbtn, submitqn;
    MediaRecorder recorder;
    String mfilename= null;
    TextView recstatus;



    private StorageReference mStorage;
    FirebaseFirestore db;
    EditText question;
    private ProgressDialog mprogress;
    private static final String LOG_TAG="Record_Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientlobby);
        mStorage = FirebaseStorage.getInstance().getReference();
        String RoomName = getIntent().getStringExtra("roomname");
        String QName = getIntent().getStringExtra("qname");
        recordbtn = (Button) findViewById(R.id.taptospeak);
        submitqn = (Button) findViewById(R.id.submitqn);
        recstatus = (TextView) findViewById(R.id.recstatus);
        mfilename = Environment.getExternalStorageDirectory().getAbsolutePath();
        mfilename+="/recorded_audio2.mpeg4";
        mfilename = mfilename.replaceAll(":", ".");
        TextView roomidfill = findViewById(R.id.roomidfill);
        roomidfill.setText(RoomName);
        TextView namefill = findViewById(R.id.namefill);
        namefill.setText("Name: "+QName);
        mprogress = new ProgressDialog(this);
        question = findViewById(R.id.question);


        recordbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){

                    try {
                        startRecording();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    recstatus.setText("Rec. Started");

                }else if(motionEvent.getAction()== MotionEvent.ACTION_UP){

                    stopRecording();
                    recstatus.setText("Rec. Stopped");



                }

                return false;
            }
        });

        submitqn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db = FirebaseFirestore.getInstance();
                String Question = question.getText().toString();
                Map<String,Object> user = new HashMap<>();


                user.put("Question : ",Question);

                db.collection(RoomName+"_tqns").document(RoomName+"_"+QName).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(clientlobby.this,"Text qn sent successfully",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(clientlobby.this, "Failed", Toast.LENGTH_SHORT).show();

                            }
                        });



            }
        });
    }




    private void startRecording() throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(mfilename);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        uploadAudio();
    }

    private void uploadAudio() {


        mprogress.setMessage("Uploading Audio...");
        mprogress.show();
        String RoomName = getIntent().getStringExtra("roomname");
        String QName = getIntent().getStringExtra("qname");
        StorageReference filepath = mStorage.child("Audio").child(RoomName+"_"+QName+".wav");
        Uri uri = Uri.fromFile( new File(mfilename));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlsong = uriTask.getResult();

                songurl = urlsong.toString();
                songname = RoomName+"_"+QName;
                uploaddetailstodb();

                db = FirebaseFirestore.getInstance();

                Map<String,Object> user = new HashMap<>();

                user.put("Songname",songname);
                user.put("Songurl",songurl);

                db.collection(RoomName).document(songname).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(clientlobby.this,"Successful",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(clientlobby.this, "Failed", Toast.LENGTH_SHORT).show();

                            }
                        });


                mprogress.dismiss();
                recstatus.setText("Uploading Finished");
            }
        });

    }

    private void uploaddetailstodb() {

        Song songobj = new Song(songname,songurl);
        FirebaseDatabase.getInstance().getReference("Songs").push().setValue(songobj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(clientlobby.this, "Audio Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(clientlobby.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });





    }


}