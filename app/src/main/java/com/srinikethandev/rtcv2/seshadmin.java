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

import com.example.jean.jcplayer.model.JcAudio;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class seshadmin extends AppCompatActivity {

    ListView listView;

    TextView quesfill;
    ArrayList<String> arrayListques = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private Button endbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seshadmin);
        quesfill =(TextView)findViewById(R.id.quesfill);

        listView = findViewById(R.id.mylistview1);
        retrievesongs();
        endbutton = (Button) findViewById(R.id.endbutton1);
        endbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opencloseactivity();
            }
        });
    }

    private void retrievesongs() {

        FirebaseFirestore dbroot;
        dbroot = FirebaseFirestore.getInstance();
        String roomname1 = getIntent().getStringExtra("roomname");
        dbroot.collection(roomname1+"_tqns_rev").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot : snapshotList) {
                    arrayListques.add(snapshot.getString("Question : "));

                    arrayAdapter = new ArrayAdapter<String>(seshadmin.this, android.R.layout.simple_list_item_1, arrayListques) {

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
                    listView.setAdapter(arrayAdapter);

                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String text =((TextView) view).getText().toString();
                        quesfill.setText(text);
                    }
                });

            }





        });


    }
    public void opencloseactivity(){

        Intent intent = new Intent(this, lobby.class);
        startActivity(intent);
    }
}