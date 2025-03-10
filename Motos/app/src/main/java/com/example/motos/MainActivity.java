package com.example.motos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los componentes del layout
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.btn_go_to_menu);
        TextView goToRegisterTextView = findViewById(R.id.tv_go_to_register);

        // Configuración de Retrofit para consumir la API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.101.10:3000/")  // Cambia la URL si es necesario
                .addConverterFactory(GsonConverterFactory.create())  // Usar GsonConverterFactory para JSON
                .build();

        apiService = retrofit.create(ApiService.class);

        // Configurar el listener del botón para realizar el login
        loginButton.setOnClickListener(view -> loginUser(emailEditText, passwordEditText));

        // Configurar el listener del botón de registro para ir a RegistroActivity
        goToRegisterTextView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(EditText emailEditText, EditText passwordEditText) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto de usuario para la solicitud de login
        User user = new User(email, password);

        // Llamada al servidor para iniciar sesión
        Call<ResponseModel> call = apiService.login(user);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    ResponseModel responseModel = response.body();
                    if (responseModel != null && responseModel.getMessage() != null) {
                        // Mostrar mensaje de éxito
                        Toast.makeText(MainActivity.this, responseModel.getMessage(), Toast.LENGTH_SHORT).show();

                        // Guardar user_id y otros datos en SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("user_id", responseModel.getUserId());
                        editor.putString("nombreUsuario", responseModel.getNombreUsuario());
                        editor.putString("correo", responseModel.getCorreo());

                        try {
                            editor.putInt("edad", Integer.parseInt(responseModel.getEdad()));
                        } catch (NumberFormatException e) {
                            Log.e("MainActivity", "Edad no es un número válido");
                            editor.putInt("edad", -1); // Valor predeterminado en caso de error
                        }
                        editor.apply();

                        // Ir a MenuActivity
                        Intent intent = new Intent(MainActivity.this, ViewsActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (responseModel != null && responseModel.getError() != null) {
                        // Mostrar mensaje de error desde el servidor
                        Toast.makeText(MainActivity.this, responseModel.getError(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Mostrar el error en caso de una respuesta fallida
                    try (ResponseBody errorBody = response.errorBody()) {
                        if (errorBody != null) {
                            String errorContent = errorBody.string();
                            Log.e("MainActivity", "Error en la respuesta: " + errorContent);
                            Toast.makeText(MainActivity.this, "Error: " + errorContent, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                Log.e("MainActivity", "Error en la conexión: " + t.getMessage());
                Toast.makeText(MainActivity.this, "No se pudo conectar al servidor. Por favor, verifica tu conexión a Internet.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
