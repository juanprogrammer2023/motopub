package com.example.motos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> postList;
    private final Context context;
    private final ApiService apiService;

    public PostAdapter(Context context, List<Post> postList, ApiService apiService) {
        this.context = context;
        this.postList = postList;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Asignar datos del post al layout
        holder.profileName.setText(post.getProfileName());
        holder.postTitle.setText(post.getTitle());
        holder.description.setText(post.getDescription());
        holder.likes.setText(String.format(Locale.getDefault(), "%d likes", post.getLikes()));

        // Calcular y mostrar el tiempo transcurrido en lugar de la fecha exacta
        String timeAgo = getTimeAgo(post.getDate());
        holder.postDate.setText(timeAgo);

        // Cargar imagen con Glide
        Glide.with(context)
                .load("http://192.168.101.10:3000" + post.getImage())
                .into(holder.postImage);

        // Limpia el contenedor de etiquetas para evitar que se acumulen etiquetas de otros posts
        holder.tagsContainer.removeAllViews();
        // Cargar etiquetas asignadas al post en el contenedor horizontal
        loadTags(post.getId(), holder.tagsContainer);

        // Cargar etiquetas disponibles en el ChipGroup
        loadAvailableTags(holder.chipGroupTags);

        // Mostrar comentarios al hacer clic en el botón de comentarios
        holder.btnComment.setOnClickListener(v -> {
            if (holder.commentsSection.getVisibility() == View.GONE) {
                holder.commentsSection.setVisibility(View.VISIBLE);
                loadComments(post.getId(), holder.commentsRecyclerView); // Cargar comentarios para este post
            } else {
                holder.commentsSection.setVisibility(View.GONE);
            }
        });

        // Funcionalidad para enviar un comentario
        holder.btonSendComment.setOnClickListener(v -> {
            String commentText = holder.etNewComment.getText().toString().trim();

            // Recuperar userId desde SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);


            if (userId != -1 && !commentText.isEmpty()) {
                addCommentToPost(post.getId(), commentText, userId, holder);
                holder.etNewComment.setText(""); // Limpiar el campo de comentario después de enviar
            } else if (userId == -1) {
                Toast.makeText(context, "Error: usuario no identificado.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "El comentario no puede estar vacío.", Toast.LENGTH_SHORT).show();
            }
        });

        // Funcionalidad para el botón de eliminación de etiquetas
        holder.deleteTagsButton.setOnClickListener(v -> {
            List<Integer> selectedTagIds = new ArrayList<>();
            for (int i = 0; i < holder.chipGroupTags.getChildCount(); i++) {
                Chip chip = (Chip) holder.chipGroupTags.getChildAt(i);
                if (chip.isChecked()) {
                    selectedTagIds.add((Integer) chip.getTag());
                }
            }

            if (!selectedTagIds.isEmpty()) {
                deleteTagsFromPost(post.getId(), selectedTagIds, holder.tagsContainer, holder.chipGroupTags, post);
            } else {
                Toast.makeText(context, "Selecciona etiquetas para eliminar.", Toast.LENGTH_SHORT).show();
            }
        });

        // Asignar etiquetas seleccionadas al post
        holder.assignTagsButton.setOnClickListener(v -> {
            List<Integer> selectedTagIds = new ArrayList<>();
            List<Integer> alreadyAssignedTagIds = new ArrayList<>();

            for (int i = 0; i < holder.chipGroupTags.getChildCount(); i++) {
                Chip chip = (Chip) holder.chipGroupTags.getChildAt(i);
                if (chip.isChecked()) {
                    int tagId = (Integer) chip.getTag();
                    if (post.getAssignedTags().contains(tagId)) {
                        alreadyAssignedTagIds.add(tagId);
                    } else {
                        selectedTagIds.add(tagId);
                    }
                }
            }

            if (!alreadyAssignedTagIds.isEmpty()) {
                Toast.makeText(context, "Algunas etiquetas ya estaban asignadas.", Toast.LENGTH_SHORT).show();
            }

            if (!selectedTagIds.isEmpty()) {
                assignTagsToPost(post.getId(), selectedTagIds, holder.tagsContainer, holder.chipGroupTags, post);
            }
        });
    }


    // Método para agregar comentario al servidor
    private void addCommentToPost(int postId, String commentText, int userId, PostViewHolder holder) {
        Call<Map<String, Object>> call = apiService.addCommentToPost(postId, commentText, userId);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Comentario agregado correctamente.", Toast.LENGTH_SHORT).show();
                    // Recargar los comentarios para ver el nuevo comentario
                    loadComments(postId, holder.commentsRecyclerView);
                } else {
                    Toast.makeText(context, "Error al agregar comentario.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Error de conexión al agregar comentario.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Método para calcular el tiempo transcurrido desde la fecha del post
     */
    private String getTimeAgo(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(dateString);
            if (date == null) return "";

            long timeInMillis = date.getTime();
            long now = System.currentTimeMillis();

            // Calcular la diferencia en milisegundos
            long diff = now - timeInMillis;

            if (diff < 60000) {
                return "hace unos segundos";
            } else if (diff < 3600000) {
                return "hace " + (diff / 60000) + " minutos";
            } else if (diff < 86400000) {
                return "hace " + (diff / 3600000) + " horas";
            } else {
                return "hace " + (diff / 86400000) + " días";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // En caso de error, muestra la fecha sin procesar
        }
    }


    // Método para cargar comentarios y configurar el RecyclerView de comentarios
    private void loadComments(int postId, RecyclerView commentsRecyclerView) {
        apiService.getCommentsForPost(postId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comment> commentList = response.body();

                    // Configurar el adaptador para el RecyclerView de comentarios
                    CommentAdapter commentAdapter = new CommentAdapter(context, commentList);
                    commentsRecyclerView.setAdapter(commentAdapter);
                    commentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }


    // Método para cargar los comentarios de un post
    private void loadComments(int postId, PostViewHolder holder) {
        Call<List<Comment>> call = apiService.getCommentsForPost(postId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comment> comments = response.body();
                    CommentAdapter commentAdapter = new CommentAdapter(context, comments);
                    holder.commentsRecyclerView.setAdapter(commentAdapter);
                } else {
                    Log.e("LoadComments", "No se pudieron cargar los comentarios.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {
                Log.e("LoadComments", "Error al cargar los comentarios", t);
            }
        });
    }

    private void loadTags(int postId, LinearLayout tagsContainer) {
        Call<List<Tag>> call = apiService.getTagsForPost(postId);
        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tag> tags = response.body();
                    tagsContainer.removeAllViews(); // Asegura que no haya etiquetas previas

                    for (Tag tag : tags) {
                        TextView tagView = new TextView(context);
                        tagView.setText(tag.getNombreEtiqueta());
                        tagView.setPadding(16, 16, 16, 16);
                        tagView.setTextColor(Color.WHITE);
                        tagView.setBackgroundResource(R.drawable.tag_background);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(12, 8, 12, 8);
                        tagView.setLayoutParams(params);

                        tagsContainer.addView(tagView);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }


    // Método para eliminar etiquetas seleccionadas del post
    private void deleteTagsFromPost(int postId, List<Integer> etiquetaIds, LinearLayout tagsContainer, ChipGroup chipGroup, Post post) {
        // Remover etiquetas del objeto post en local
        for (int id : etiquetaIds) {
            post.removeAssignedTag(id);
        }

        // Limpiar y actualizar el contenedor de etiquetas
        tagsContainer.removeAllViews();
        updateTagsContainer(post.getAssignedTags(), tagsContainer, chipGroup);

        // Enviar la solicitud al servidor para eliminar las etiquetas
        Map<String, List<Integer>> requestBody = new HashMap<>();
        requestBody.put("etiquetaIds", etiquetaIds);

        apiService.deleteTagsFromPost(postId, requestBody).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Etiquetas eliminadas correctamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("DeleteTags", "Error al eliminar etiquetas: " + response.message());
                    Toast.makeText(context, "Error al eliminar etiquetas.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Error de conexión al eliminar etiquetas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAvailableTags(ChipGroup chipGroup) {
        apiService.getAvailableTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tag> tags = response.body();
                    chipGroup.removeAllViews();

                    for (Tag tag : tags) {
                        Chip chip = new Chip(context);
                        chip.setText(tag.getNombreEtiqueta());
                        chip.setCheckable(true);
                        chip.setCheckedIconVisible(false);
                        chip.setTextColor(Color.BLACK);
                        chip.setTag(tag.getId());

                        chip.setTextSize(12);
                        chip.setChipCornerRadius(12);
                        chip.setChipStrokeWidth(1f);
                        chip.setChipStrokeColorResource(R.color.purple_700);
                        chip.setChipBackgroundColorResource(R.color.juan);

                        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) {
                                chip.setChipBackgroundColorResource(R.color.selected_chip_background);
                                chip.setTextColor(Color.WHITE);
                            } else {
                                chip.setChipBackgroundColorResource(R.color.juan);
                                chip.setTextColor(Color.BLACK);
                            }
                        });

                        chipGroup.addView(chip);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void assignTagsToPost(int postId, List<Integer> etiquetaIds, LinearLayout tagsContainer, ChipGroup chipGroup, Post post) {
        Map<String, List<Integer>> requestBody = new HashMap<>();
        requestBody.put("etiquetaIds", etiquetaIds);

        apiService.assignTagsToPost(postId, requestBody).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();

                    List<Integer> alreadyAssigned = new ArrayList<>();
                    List<Integer> newlyAssigned = new ArrayList<>();

                    if (responseBody.get("alreadyAssigned") instanceof List) {
                        for (Object id : (List<?>) responseBody.get("alreadyAssigned")) {
                            if (id instanceof Number) {
                                alreadyAssigned.add(((Number) id).intValue());
                            }
                        }
                    }

                    if (responseBody.get("newlyAssigned") instanceof List) {
                        for (Object id : (List<?>) responseBody.get("newlyAssigned")) {
                            if (id instanceof Number) {
                                newlyAssigned.add(((Number) id).intValue());
                            }
                        }
                    }

                    if (!alreadyAssigned.isEmpty()) {
                        Toast.makeText(context, "Algunas etiquetas ya estaban asignadas.", Toast.LENGTH_SHORT).show();
                    }

                    if (!newlyAssigned.isEmpty()) {
                        for (int id : newlyAssigned) {
                            post.addAssignedTag(id);
                        }
                        Toast.makeText(context, "Etiquetas asignadas correctamente.", Toast.LENGTH_SHORT).show();
                    }

                    updateTagsContainer(post.getAssignedTags(), tagsContainer, chipGroup);
                } else if (response.code() == 409) {
                    Toast.makeText(context, "Algunas etiquetas ya están asignadas.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("AssignTags", "Error al asignar etiquetas: " + response.message());
                    Toast.makeText(context, "Error al asignar etiquetas.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Error de conexión al asignar etiquetas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTagsContainer(List<Integer> etiquetaIds, LinearLayout tagsContainer, ChipGroup chipGroup) {
        for (int tagId : etiquetaIds) {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if ((int) chip.getTag() == tagId) {
                    TextView tagView = new TextView(context);
                    tagView.setText(chip.getText().toString());
                    tagView.setPadding(16, 16, 16, 16);
                    tagView.setTextColor(Color.WHITE);
                    tagView.setBackgroundResource(R.drawable.tag_background);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(12, 8, 12, 8);
                    tagView.setLayoutParams(params);

                    tagsContainer.addView(tagView);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView profileName, postTitle, likes, description, postDate;
        ImageView postImage;
        LinearLayout tagsContainer;
        ChipGroup chipGroupTags;
        Button assignTagsButton, deleteTagsButton;
        ImageView btnComment;
        LinearLayout commentsSection;
        RecyclerView commentsRecyclerView;

        EditText etNewComment;
        Button btonSendComment;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.tv_profile_name);
            postTitle = itemView.findViewById(R.id.tv_post_title);
            postImage = itemView.findViewById(R.id.img_post_image);
            likes = itemView.findViewById(R.id.tv_likes);
            description = itemView.findViewById(R.id.tv_description);
            postDate = itemView.findViewById(R.id.tv_post_date);
            tagsContainer = itemView.findViewById(R.id.tags_container);
            chipGroupTags = itemView.findViewById(R.id.chip_group_tags);
            assignTagsButton = itemView.findViewById(R.id.btn_assign_tags);
            deleteTagsButton = itemView.findViewById(R.id.btnDelete);
            btnComment = itemView.findViewById(R.id.btn_comment);
            commentsSection = itemView.findViewById(R.id.comments_section);
            commentsRecyclerView = itemView.findViewById(R.id.recycler_view_comments);
            etNewComment = itemView.findViewById(R.id.et_new_comment);
            btonSendComment = itemView.findViewById(R.id.btn_send_comment);

        }
    }
}
