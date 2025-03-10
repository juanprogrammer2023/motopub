package com.example.motos;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface ApiService {

    // Para registrar un usuario
    @POST("/register")
    Call<ResponseModel> register(@Body User user);

    // Para iniciar sesión
    @POST("/login")
    Call<ResponseModel> login(@Body User user);

    // Método para obtener los detalles del usuario por su correo electrónico
    @GET("/user/{email}")
    Call<User> getUserByEmail(@Path("email") String email);

    // Para crear un nuevo post con imagen
    @Multipart
    @POST("/posts")
    Call<ResponseModel> createPost(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part image
    );

    @GET("/posts/{postId}/etiquetas")
    Call<List<Tag>> getTagsForPost(@Path("postId") int postId);

    // Obtener todas las etiquetas disponibles
    @GET("/tags")
    Call<List<Tag>> getAvailableTags();

    // Asignar etiquetas a un post específico
    @POST("/posts/{postId}/tags")
    Call<Map<String, Object>> assignTagsToPost(
            @Path("postId") int postId,
            @Body Map<String, List<Integer>> etiquetaIds
    );

    @HTTP(method = "DELETE", path = "/posts/{postId}/tags", hasBody = true)
    Call<Map<String, Object>> deleteTagsFromPost(@Path("postId") int postId, @Body Map<String, List<Integer>> etiquetaIds);


    // Para obtener todos los posts
    @GET("/posts")
    Call<List<Post>> getAllPosts();

    @GET("/posts/{postId}/comments")
    Call<List<Comment>> getCommentsForPost(@Path("postId") int postId);

    @FormUrlEncoded
    @POST("/posts/{postId}/comments")
    Call<Map<String, Object>> addCommentToPost(
            @Path("postId") int postId,
            @Field("comment_text") String commentText,
            @Field("usuario_id") int userId
    );


}
