package com.example.motos;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnSelectImage, btnCreatePost;
    private Uri selectedImageUri;
    private File selectedImageFile;
    private int userId;

    // Launcher para seleccionar una imagen
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    selectedImageFile = createTempFileFromUri(selectedImageUri);
                    if (selectedImageFile == null) {
                        Toast.makeText(this, "Error al obtener el archivo de imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnCreatePost = findViewById(R.id.btn_create_post);

        // Recuperar userId desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID del usuario no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSelectImage.setOnClickListener(v -> openImageChooser());
        btnCreatePost.setOnClickListener(v -> createPost());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private File createTempFileFromUri(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = new File(getCacheDir(), "temp_image.jpg");
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            inputStream.close();

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createPost() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || selectedImageFile == null) {
            Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Preparar los datos para Retrofit
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), selectedImageFile);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", selectedImageFile.getName(), requestFile);

        // Llamada a la API
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ResponseModel> call = apiService.createPost(titleBody, descriptionBody, userIdBody, image);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreatePostActivity.this, "Post creado correctamente.", Toast.LENGTH_SHORT).show();
                    finish(); // Finalizar actividad
                } else {
                    Toast.makeText(CreatePostActivity.this, "Error al crear el post.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                Toast.makeText(CreatePostActivity.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
