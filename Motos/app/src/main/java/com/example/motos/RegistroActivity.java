package com.example.motos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegistroActivity extends AppCompatActivity {

    private EditText nombreUsuarioEditText, correoUsuarioEditText, contrasenaEditText, edadEditText;
    private Button btnRegister;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Referenciar los campos del formulario
        nombreUsuarioEditText = findViewById(R.id.nombreUsuario);
        correoUsuarioEditText = findViewById(R.id.CorreoUsuario);
        contrasenaEditText = findViewById(R.id.contrasena);
        edadEditText = findViewById(R.id.edad);
        btnRegister = findViewById(R.id.btn_register);

        Gson gson = new GsonBuilder()
                .setLenient() // Permite JSON menos estricto
                .create();

        // Configuración de Retrofit para manejar JSON
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.101.10:3000")  // Cambia la URL si es necesario
                .addConverterFactory(GsonConverterFactory.create(gson))  // Usar GsonConverterFactory para JSON
                .build();

        // Crear instancia de ApiService
        apiService = retrofit.create(ApiService.class);

        // Configurar la acción del botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Obtener los datos del formulario
        String nombreUsuario = nombreUsuarioEditText.getText().toString().trim();
        String correoUsuario = correoUsuarioEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString().trim();
        String edadStr = edadEditText.getText().toString().trim();

        if (nombreUsuario.isEmpty() || correoUsuario.isEmpty() || contrasena.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad = Integer.parseInt(edadStr); // Convertir la edad a número

        // Crear objeto de usuario para el registro
        User user = new User(nombreUsuario, correoUsuario, contrasena, edad);

        // Llamada al servidor para registrar el usuario
        Call<ResponseModel> call = apiService.register(user);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    ResponseModel responseModel = response.body();
                    if (responseModel != null) {
                        if (responseModel.getMessage() != null) {
                            // Éxito: muestra el mensaje de éxito
                            Toast.makeText(RegistroActivity.this, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else if (responseModel.getError() != null) {
                            // Error: muestra el mensaje de error
                            Toast.makeText(RegistroActivity.this, responseModel.getError(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Manejar errores de la respuesta
                    try {
                        String errorBody = response.errorBody().string();
                        int errorCode = response.code(); // Código de estado HTTP (400, 500, etc.)
                        Log.e("RegistroActivity", "Error en la respuesta. Código: " + errorCode + ", Cuerpo: " + errorBody);

                        // Mostrar un mensaje más específico
                        if (errorCode == 400) {
                            Toast.makeText(RegistroActivity.this, "Datos incompletos: " + errorBody, Toast.LENGTH_LONG).show();
                        } else if (errorCode == 500) {
                            Toast.makeText(RegistroActivity.this, "Error en el servidor. Inténtalo más tarde.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error inesperado: " + errorBody, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegistroActivity.this, "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("RegistroActivity", "Error en la conexión: " + t.getMessage(), t);
                Toast.makeText(RegistroActivity.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
