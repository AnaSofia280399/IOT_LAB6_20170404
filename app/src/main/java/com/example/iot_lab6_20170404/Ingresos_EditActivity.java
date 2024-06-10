package com.example.iot_lab6_20170404;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iot_lab6_20170404.dto.Ingreso;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Ingresos_EditActivity extends AppCompatActivity {


    TextView edit_titulo, edit_fecha;
    String titulo, fecha;


    //-----FIREBASE--------

    FirebaseFirestore db;
    FirebaseUser currentUser;



    //----------

    Button guardar_editButton;
    EditText  edit_monto, edit_descripcion;

    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresos_edit);



        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);

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

        //para obtener el objeto a editar

        Bundle bundle = getIntent().getExtras();

        String id = bundle.getString("id");

        //relacionamos la vista

        edit_descripcion = findViewById(R.id.descripcion_editIngreso);
        edit_fecha = findViewById(R.id.fecha_editIngreso);
        edit_titulo = findViewById(R.id.titulo_editIngreso);
        edit_monto = findViewById(R.id.monto_editIngreso);
        String uid = currentUser.getUid();

        guardar_editButton = findViewById(R.id.boton_guardarEditIngreso);

        //buscamos objeto a editar


        if (bundle != null){
            db.collection("usuarios_por_auth")
                    .document(uid)
                    .collection("ingresos")
                    .whereEqualTo("id", id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                titulo = document.getString("titulo");
                                fecha = document.getString("fecha");
                                String descripcion = document.getString("descripcion");

                                //vere si funciona monto

                                edit_titulo.setText(titulo);
                                edit_fecha.setText(fecha);
                                edit_descripcion.setText(descripcion);

                                Double monto = document.getDouble("monto"); // Corregido aquí

                                edit_monto.setText(String.format("%.2f", monto));
                            } else {
                                Toast.makeText(Ingresos_EditActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        guardar_editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Ingresos_EditActivity.this);
                builder.setCancelable(false);
                builder.setView(R.layout.progress_layout);
                AlertDialog dialog = builder.create();
                dialog.show();

                //se muestra el dialog

                String descripcion = edit_descripcion.getText().toString();
                Double monto = Double.parseDouble(edit_monto.getText().toString());

                if(currentUser != null){
                    // Buscar el ingreso específico por ID
                    db.collection("usuarios_por_auth")
                            .document(uid)
                            .collection("ingresos")
                            .whereEqualTo("id", id)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    // Suponiendo que solo hay un documento que coincida con el ID
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    DocumentReference docRef = document.getReference();

                                    // Crear un nuevo objeto ingreso con los datos actualizados
                                    Ingreso ingreso = new Ingreso(descripcion, fecha, titulo, monto, id);

                                    // Actualizar el documento encontrado
                                    docRef.set(ingreso)
                                            .addOnSuccessListener(aVoid -> Toast.makeText(Ingresos_EditActivity.this, "Ingreso actualizado", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(Ingresos_EditActivity.this, "Error al actualizar ingreso", Toast.LENGTH_SHORT).show());
                                } else {
                                    Toast.makeText(Ingresos_EditActivity.this, "Ingreso no encontrado", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Ingresos_EditActivity.this, "No está logueado", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();

                Intent intent = new Intent(Ingresos_EditActivity.this, MainActivity.class);
                startActivity(intent);


            }
        });
    }
}