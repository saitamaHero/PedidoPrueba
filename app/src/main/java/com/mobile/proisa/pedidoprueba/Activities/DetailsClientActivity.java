package com.mobile.proisa.pedidoprueba.Activities;

import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.proisa.pedidoprueba.R;

import java.util.Locale;

import Models.Client;

public class DetailsClientActivity extends AppCompatActivity {
    public static final String EXTRA_CLIENT = "client";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_client);

        Client client = getIntent().getExtras().getParcelable(EXTRA_CLIENT);

        if(client == null){
            finish();
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        loadBackdrop(client.getProfilePhoto());
        loadInfo(client);
    }

    private void loadBackdrop(Uri uri) {
        final ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this)
                .load(uri)
                .apply(RequestOptions.placeholderOf(getResources().getDrawable(R.drawable.ic_account_user)))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);


    }

    private void loadInfo(Client client){
        TextView txtName = findViewById(R.id.client_name);
        txtName.setText(client.getName());

        TextView txtId = findViewById(R.id.client_id);
        txtId.setText(client.getId());

        TextView txtDistance = findViewById(R.id.distance);
        txtDistance.setText(String.format(Locale.getDefault(),"%.2f Km",client.getDistance()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
