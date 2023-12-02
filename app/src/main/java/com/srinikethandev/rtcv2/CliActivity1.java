package com.srinikethandev.rtcv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CliActivity1 extends AppCompatActivity {

    private Button button;
    EditText roomname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cli1);

        button = (Button) findViewById(R.id.cli1button1);
        roomname=findViewById(R.id.entrname);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openactivity1();
            }
        });
    }

    public void openactivity1(){



        Intent intent = new Intent(this, lobby.class);
        intent.putExtra("roomname",roomname.getText().toString());
        startActivity(intent);
    }
}