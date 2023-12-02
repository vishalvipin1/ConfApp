package com.srinikethandev.rtcv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class lobby extends AppCompatActivity {

    ListView listView;

    ArrayList<String> arrayListsongsname = new ArrayList<>();
    ArrayList<String> arrayListsongsurl = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    JcPlayerView jcPlayerView;
    private Button button;
    TextView roomfill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        roomfill= findViewById(R.id.roomfill);
        String roomname = getIntent().getStringExtra("roomname");
        roomfill.setText("Room : "+roomname);
        listView = findViewById(R.id.mylistview);
        jcPlayerView = findViewById(R.id.jcplayer);

        retrievesongs();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                jcPlayerView.playAudio(jcAudios.get(i));
                jcPlayerView.setVisibility(View.VISIBLE);

            }
        });

        button = (Button) findViewById(R.id.startseshbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openactivity();
            }
        });
    }

    private void retrievesongs() {

        FirebaseFirestore dbroot;
        dbroot = FirebaseFirestore.getInstance();
        String roomname = getIntent().getStringExtra("roomname");
        dbroot.collection(roomname).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot : snapshotList) {
                    Song songobj = new Song(snapshot.getString("Songname"), snapshot.getString("Songurl"));
                    arrayListsongsname.add(songobj.getSongname());
                    arrayListsongsurl.add(songobj.getSongurl());
                    jcAudios.add(JcAudio.createFromURL(songobj.getSongname(), songobj.getSongurl()));

                    arrayAdapter = new ArrayAdapter<String>(lobby.this, android.R.layout.simple_list_item_1, arrayListsongsname) {

                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                            View view = super.getView(position, convertView, parent);
                            TextView textView = (TextView) view.findViewById(android.R.id.text1);

                            textView.setSingleLine(true);
                            textView.setMaxLines(1);

                            return super.getView(position, convertView, parent);
                        }
                    };
                    jcPlayerView.initPlaylist(jcAudios, null);
                    listView.setAdapter(arrayAdapter);
                }

            }





        });


    }

    public void openactivity() {

        Intent intent = new Intent(this, seshadmin.class);
        String roomname1 = getIntent().getStringExtra("roomname");
        intent.putExtra("roomname",roomname1);
        startActivity(intent);

    }

}