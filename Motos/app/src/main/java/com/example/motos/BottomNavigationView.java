package com.example.motos;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class BottomNavigationView extends RelativeLayout {

    private ImageView homeIcon;
    private ImageView searchIcon;
    private ImageView addIcon;
    private ImageView notificationIcon;
    private ImageView profileIcon;

    public BottomNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Inflar el layout de la barra de navegación
        LayoutInflater.from(context).inflate(R.layout.buttonnavigation, this, true);

        // Inicializar los iconos
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addIcon = findViewById(R.id.addIcon);
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);

        // Listener para redirigir a ViewsActivity cuando se presione el botón de home
        homeIcon.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewsActivity.class);
            context.startActivity(intent);
        });

        // Listener para redirigir a CreatePostActivity cuando se presione el botón de agregar
        addIcon.setOnClickListener(view -> {
            Intent intent = new Intent(context, CreatePostActivity.class);
            context.startActivity(intent);
        });

        profileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(context, MenuActivity.class);
            context.startActivity(intent);
        });

        // Puedes agregar otros listeners para los demás iconos, si es necesario
    }

    // Métodos para obtener los iconos si necesitas manipularlos externamente
    public ImageView getHomeIcon() { return homeIcon; }
    public ImageView getSearchIcon() { return searchIcon; }
    public ImageView getAddIcon() { return addIcon; }
    public ImageView getNotificationIcon() { return notificationIcon; }
    public ImageView getProfileIcon() { return profileIcon; }
}
