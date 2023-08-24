package com.na_at.fad.randomnumberlab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivityInstant extends AppCompatActivity {

    TextView textView;

    List<String> permissions = new ArrayList<>();

    private static final int ALL_PERMISSIONS_RESULT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_instant);

        textView = findViewById(R.id.textShowNumber);
        findViewById(R.id.button).setOnClickListener(view -> {
            permissions.clear();

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED))
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED))
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED))
                permissions.add(Manifest.permission.CAMERA);

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED))
                permissions.add(Manifest.permission.RECORD_AUDIO);

            String[] permissionsToRequest = getPermissionsToRequest();
            if (permissionsToRequest.length > 0) {
                ActivityCompat.requestPermissions(this, permissionsToRequest, ALL_PERMISSIONS_RESULT);
            } else {
                Toast.makeText(this, "Permisos ya otorgados", Toast.LENGTH_SHORT).show();
            }

        });
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

    private String[] getPermissionsToRequest() {
        // Lista para almacenar los permisos que aún no se han otorgado
        List<String> permissionsToRequestList = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequestList.add(permission);
            }
        }

        // Convertir la lista a un array
        return permissionsToRequestList.toArray(new String[0]);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            // Verificar si todos los permisos solicitados fueron otorgados
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                Toast.makeText(this, "LOS PERMISOS FUERON OTORGADOS", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "FALTAN PERMISOS", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}