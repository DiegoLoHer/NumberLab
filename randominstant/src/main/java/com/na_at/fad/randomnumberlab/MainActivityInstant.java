package com.na_at.fad.randomnumberlab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivityInstant extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_instant);

        textView = findViewById(R.id.textShowNumber);
        findViewById(R.id.button).setOnClickListener(view -> textView.setText("" + Math.random() * 49 + 1));
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData != null && appLinkAction != null) {
            String url = appLinkData.toString();
            String[] splitURL = url.split("=");
            if (splitURL.length > 1) {
                textView.setText("" + splitURL[1]);
            } else {
                textView.setText("Error en la URL");
            }
        } else {
            textView.setText("appLinkData nulll");
        }
    }
}