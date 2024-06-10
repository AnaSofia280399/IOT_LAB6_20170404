package com.example.iot_lab6_20170404;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab6_20170404.dto.Ingreso;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

// para FIREBASE----------------

    FirebaseFirestore db;

    FirebaseUser currentUser;

    DatabaseReference databaseReference;
    //----------

    //---Recycler View-------

    RecyclerView recyclerView;

    List<Ingreso> dataList;

    MyAdapter_Ingresos adapter;

    SearchView searchView;
    //------------------------

    //Google----------------------------

    TextView userName;

    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userName = findViewById(R.id.userName_ingreso);


        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (gAccount != null){
            String gname = gAccount.getDisplayName();
            userName.setText(gname);

        }


        //Navigation Bottom Logica ---------------------------

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_ingresos);

        Map<String, Intent> navigationMap = new HashMap<>();
        navigationMap.put("bottom_egresos", new Intent(getApplicationContext(), EgresosActivity.class));
        navigationMap.put("bottom_resumen", new Intent(getApplicationContext(), ResumenActivity.class));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            String itemId = getResources().getResourceEntryName(item.getItemId());
            if ("bottom_logout".equals(itemId)) {
                gClient.signOut().addOnCompleteListener(task -> {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                });
                return true; // Detener el procesamiento si es logout
            }
            if (navigationMap.containsKey(itemId)) {
                Intent intent = navigationMap.get(itemId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });


        //----------------------------------------------------



        recyclerView = findViewById(R.id.recyclerView_Ingresos);
        searchView = findViewById(R.id.search_ingresos);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //para crear un nuevo ingreso------

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new MyAdapter_Ingresos(this, dataList);
        recyclerView.setAdapter(adapter);

        //Firebase-----

        databaseReference = FirebaseDatabase.getInstance().getReference("ingresos");

        dialog.show();

        String uid = currentUser.getUid();



        db.collection("usuarios_por_auth")
                .document(uid)
                .collection("ingresos")
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
                            Ingreso ingreso = doc.toObject(Ingreso.class);
                            dataList.add(ingreso);
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();

                        // Muestra un Toast con el tamaño de la lista actualizada
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

        FloatingActionButton addIngresoButton = findViewById(R.id.floatingButton_addIngreso);
        addIngresoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define la nueva Activity que quieres abrir
                Intent intent = new Intent(MainActivity.this,Ingreso_NuevoActivity.class);  // Asume que NewSuperActivity es la actividad a la que quieres ir.
                startActivity(intent);
            }
        });







    }

    private void searchList(String text) {
        ArrayList<Ingreso> dataSearchList = new ArrayList<>();
        for (Ingreso data : dataList) {
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