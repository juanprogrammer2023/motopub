package com.example.motos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private static final String TAG = "ViewsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views);

        // Verifica si el View con el ID `main` existe antes de configurar el listener
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.e(TAG, "View con ID 'main' no encontrado en el layout activity_views.xml");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchPosts();
    }

    private void fetchPosts() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<Post>> call = apiService.getAllPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> postList = response.body();

                    // Log para ver la cantidad de posts recibidos
                    Log.d(TAG, "Número de posts recibidos: " + postList.size());

                    // Log para ver los datos de cada post sin el ID
                    for (Post post : postList) {
                        Log.d(TAG, "Title: " + post.getTitle() +
                                ", Description: " + post.getDescription() +
                                ", Profile Name: " + post.getProfileName() +
                                ", Likes: " + post.getLikes());
                    }

                    postAdapter = new PostAdapter(ViewsActivity.this, postList, apiService);
                    recyclerView.setAdapter(postAdapter);
                } else {
                    Toast.makeText(ViewsActivity.this, "Error al obtener los posts", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error en la respuesta: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(ViewsActivity.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error de conexión: ", t);
            }
        });
    }
}
