package com.jaison.app_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jaison.app_android.auth.AuthActivity;
import com.jaison.app_android.data.DataActivity;
import com.jaison.app_android.filestore.FilestoreActivity;
import com.jaison.app_android.websockets.WebsocketActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        findViewById(R.id.auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthActivity.startActivity(LauncherActivity.this);
            }
        });

        findViewById(R.id.data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataActivity.startActivity(LauncherActivity.this);
            }
        });

        findViewById(R.id.filestore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilestoreActivity.startActivity(LauncherActivity.this);
            }
        });
        findViewById(R.id.websocket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebsocketActivity.startActivity(LauncherActivity.this);
            }
        });
    }
}
