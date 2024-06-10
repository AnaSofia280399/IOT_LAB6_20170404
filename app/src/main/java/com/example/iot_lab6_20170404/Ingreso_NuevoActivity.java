package com.example.iot_lab6_20170404;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Ingreso_NuevoActivity extends AppCompatActivity {


    TextView uevotitulo;

    TextInputEditText ingreso_nuevofecha,ingreso_nuevotitulo, ingreso_nuevomonto, ingreso_nuevodescripcion;

    //-----FIREBASE--------

    FirebaseFirestore db;
    FirebaseUser currentUser;



    //----------

    Button guardarButton;
    EditText nuevo_titulo, nuevo_monto, nuevo_descripcion, nuevo_fecha;
    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_nuevo);

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

        //relacionamos la vista

        nuevo_descripcion = findViewById(R.id.descripcion_nuevoIngreso);
        nuevo_fecha = findViewById(R.id.fecha_nuevoIngreso);
        nuevo_titulo = findViewById(R.id.titulo_nuevoIngreso);
        nuevo_monto = findViewById(R.id.monto_nuevoIngreso);
        String uid = currentUser.getUid();




        guardarButton = findViewById(R.id.boton_guardarNuevoIngreso);

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Ingreso_NuevoActivity.this);
                builder.setCancelable(false);
                builder.setView(R.layout.progress_layout);
                AlertDialog dialog = builder.create();
                dialog.show();

                String titulo = nuevo_titulo.getText().toString();
                String descripcion = nuevo_descripcion.getText().toString();
                Double monto = Double.parseDouble(nuevo_monto.getText().toString());



                //fecha-logica---------------------

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.add(Calendar.HOUR_OF_DAY, -5); // Restar 5 horas

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Format date and time
                String dateTime = day + "/" + month + "/" + year + " " + hour + ":" + String.format("%02d", minute);

                // Get or set date and time for registro and edicion
                String fecha_registro = nuevo_fecha.getText().toString();
                if (fecha_registro.isEmpty()) {
                    fecha_registro = dateTime;
                } else {
                    fecha_registro += " " + hour + ":" + String.format("%02d", minute);
                }

                //fecha----fin

                Ingreso ingreso = new Ingreso(descripcion, fecha_registro,titulo, monto , fecha_registro + String.format("%.2f", monto));

                if(currentUser != null){

                    db.collection("usuarios_por_auth")
                            .document(uid)
                            .collection("ingresos")
                            .add(ingreso)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(Ingreso_NuevoActivity.this, "Guardado", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->{
                                Toast.makeText(Ingreso_NuevoActivity.this, "Algo paso al guardar", Toast.LENGTH_SHORT).show();

                            });
                }else {
                    Toast.makeText(Ingreso_NuevoActivity.this, "No esta logueado", Toast.LENGTH_SHORT).show();

                }

                nuevo_fecha.setOnClickListener(view -> showDatePickerDialog(ingreso_nuevofecha));



                // Aquí se subirán directamente los datos sin manejar imágenes


                dialog.dismiss();
                Intent intent = new Intent(Ingreso_NuevoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }



    private void showDatePickerDialog(final TextInputEditText dateField) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    dateField.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

}