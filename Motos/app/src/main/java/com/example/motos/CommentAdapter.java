package com.example.motos;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.commentText.setText(comment.getCommentText());
        holder.commentAuthor.setText(comment.getNombreUsuario());

        // Calcular el tiempo transcurrido usando el campo "created_at"
        String elapsedTime = getElapsedTime(comment.getCreatedAt());
        holder.commentDate.setText(elapsedTime);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText, commentDate, commentAuthor;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.tv_comment_text);
            commentDate = itemView.findViewById(R.id.tv_comment_date);
            commentAuthor = itemView.findViewById(R.id.tv_comment_author);
        }
    }

    // Método para calcular el tiempo transcurrido desde la fecha "created_at"
    private String getElapsedTime(String createdAt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Interpretar la fecha como UTC
        try {
            long commentTime = dateFormat.parse(createdAt).getTime();
            long currentTime = System.currentTimeMillis();

            // Generar una cadena de tiempo relativa, como "hace 2 horas"
            return DateUtils.getRelativeTimeSpanString(
                    commentTime,
                    currentTime,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return createdAt; // En caso de error, se devuelve la fecha original
        }
    }
}
