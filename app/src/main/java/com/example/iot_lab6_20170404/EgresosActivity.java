package com.example.iot_lab6_20170404;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab6_20170404.dto.Egreso;
import com.example.iot_lab6_20170404.dto.Ingreso;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EgresosActivity extends AppCompatActivity {

    FirebaseFirestore db;

    FirebaseUser currentUser;

    DatabaseReference databaseReference;
    //----------

    //---Recycler View-------

    RecyclerView recyclerView;

    List<Egreso> dataList;

    MyAdapter_Egresos adapter;

    SearchView searchView;
    //------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egresos);

        //Navigation Bottom Logica ---------------------------

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_egresos); //item seleccionado, pagina usada


        Map<String, Intent> navigationMap = new HashMap<>();
        navigationMap.put("bottom_ingresos", new Intent(getApplicationContext(), MainActivity.class));
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

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView_egresos);
        searchView = findViewById(R.id.search_egresos);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //para crear un nuevo ingreso------

        AlertDialog.Builder builder = new AlertDialog.Builder(EgresosActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new MyAdapter_Egresos(this, dataList);
        recyclerView.setAdapter(adapter);

        //Firebase-----

        databaseReference = FirebaseDatabase.getInstance().getReference("egresos");

        dialog.show();

        String uid = currentUser.getUid();



        db.collection("usuarios_por_auth")
                .document(uid)
                .collection("egresos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore Error", "listen:error", e);
                            return;
                        }

                        dataList.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Egreso egreso = doc.toObject(Egreso.class);
                            dataList.add(egreso);
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();

                        // Muestra un Toast con el tamaño de la lista actualizada
                        Toast.makeText(getApplicationContext(), "Número de egresos: " + dataList.size(), Toast.LENGTH_LONG).show();
                    }
                });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchList(newText);

                return true;
            }
        });

        //---------------------------

        FloatingActionButton addEgresoButton = findViewById(R.id.floatingButton_addEgreso);
        addEgresoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define la nueva Activity que quieres abrir
                Intent intent = new Intent(EgresosActivity.this,Egresos_NuevoActivity.class);  // Asume que NewSuperActivity es la actividad a la que quieres ir.
                startActivity(intent);
            }
        });







    }

    private void searchList(String text) {
        ArrayList<Egreso> dataSearchList = new ArrayList<>();
        for (Egreso data : dataList) {
            if (data.getTitulo().toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()) {
            Toast.makeText(this, "No se ha encontrado", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dataSearchList);
        }



    }
}