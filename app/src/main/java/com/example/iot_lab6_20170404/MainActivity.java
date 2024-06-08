package com.example.iot_lab6_20170404;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Navigation Bottom Logica ---------------------------

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_ingresos);

        Map<String, Intent> navigationMap = new HashMap<>();
        navigationMap.put("bottom_egresos", new Intent(getApplicationContext(), EgresosActivity.class));
        navigationMap.put("bottom_resumen", new Intent(getApplicationContext(), ResumenActivity.class));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            String itemId = getResources().getResourceEntryName(item.getItemId());
            if (navigationMap.containsKey(itemId)) {
                startActivity(navigationMap.get(itemId));
                overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });


        //----------------------------------------------------

    }
}