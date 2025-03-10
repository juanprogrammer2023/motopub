package com.example.motos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity {

    private TextView tvNombre, tvCorreo, tvEdad, tvPassword;
    private Button btnLogout, btnCatalogo;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Referenciar los TextView y Button del layout
        tvNombre = findViewById(R.id.tv_nombre);
        tvCorreo = findViewById(R.id.tv_correo);
        tvEdad = findViewById(R.id.tv_edad);
        tvPassword = findViewById(R.id.tv_password);
        btnLogout = findViewById(R.id.btn_logout1);// Asegúrate de que el ID sea el correcto en el XML

        // Configuración de Retrofit para consumir la API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.101.10:3000")  // Cambia la URL si es necesario
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Obtener el correo del Intent o SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String correoUsuario = sharedPreferences.getString("correo", null);

        if (correoUsuario != null) {
            // Hacer una consulta al servidor para obtener los detalles del usuario
            obtenerDatosUsuario(correoUsuario);
        } else {
            Toast.makeText(this, "Correo no disponible. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Configurar el botón de logout para volver al login y limpiar sesión
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Limpia los datos de sesión
            editor.apply();

            Intent logoutIntent = new Intent(MenuActivity.this, MainActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
        });
    }

    private void obtenerDatosUsuario(@NonNull String correo) {
        // Hacer la solicitud al servidor para obtener los datos del usuario
        Call<User> call = apiService.getUserByEmail(correo);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User usuario = response.body();
                    tvNombre.setText(usuario.getNombreUsuario());
                    tvCorreo.setText(usuario.getCorreo());
                    tvEdad.setText(String.valueOf(usuario.getEdad()));
                    tvPassword.setText("********");  // No mostrar la contraseña real
                } else {
                    Toast.makeText(MenuActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(MenuActivity.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
