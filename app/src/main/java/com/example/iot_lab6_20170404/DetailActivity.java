package com.example.iot_lab6_20170404;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailActivity extends AppCompatActivity {

    TextView ingreso_titulo, ingreso_monto, ingreso_fecha, ingreso_descripcion;

    //-----FIREBASE--------

    FirebaseFirestore db;
    FirebaseUser currentUser;


    //----------

    Button editButton, deleteButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        //para poder saber que elemento mostrar

        String id = getIntent().getStringExtra("id");


        //relacionamos la vista

        ingreso_descripcion = findViewById(R.id.detailDesc);
        ingreso_fecha = findViewById(R.id.detailFecha);
        ingreso_titulo = findViewById(R.id.detailTitle);
        ingreso_monto = findViewById(R.id.detailMonto);

        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);


        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

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
                                String titulo = document.getString("titulo");
                                String fecha = document.getString("fecha");
                                String descripcion = document.getString("descripcion");

                                //vere si funciona monto

                                ingreso_titulo.setText(titulo);
                                ingreso_fecha.setText(fecha);
                                ingreso_descripcion.setText(descripcion);

                                Double monto = document.getDouble("monto"); // Corregido aquí

                                ingreso_monto.setText(String.format("%.2f", monto));

                            } else {
                                Toast.makeText(DetailActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, Ingresos_EditActivity.class)
                        // .putExtra("Nombre", perfil_superNombre.getText().toString())
                        // .putExtra("Apellido", perfil_superApellido.getText().toString())
                        .putExtra("id", id);

                        //.putExtra("DNI", perfil_superDNI.getText().toString())
                        //.putExtra("Image", imageUrl)
                        //.putExtra("Telefono", perfil_superTelefono.getText().toString())
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
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

                                    // Eliminar el documento encontrado
                                    docRef.delete()
                                            .addOnSuccessListener(aVoid -> Toast.makeText(DetailActivity.this, "Ingreso eliminado", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(DetailActivity.this, "Error al eliminar ingreso", Toast.LENGTH_SHORT).show());
                                } else {
                                    Toast.makeText(DetailActivity.this, "Ingreso no encontrado", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(DetailActivity.this, "No está logueado", Toast.LENGTH_SHORT).show();
                }


                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });





    }
}